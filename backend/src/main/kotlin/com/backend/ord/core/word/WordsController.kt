package com.backend.ord.core.word

import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.core.auth.jwt.JwtService
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.core.word.api.requests.dto.*
import com.backend.ord.core.word.api.requests.enums.WordToggleableProperty
import com.backend.ord.core.word.api.responses.dto.SingleWordResponse
import com.backend.ord.core.word.api.responses.dto.WordListItem
import com.backend.ord.core.word.model.WordDTO
import com.backend.ord.core.word.model.WordEntity
import com.backend.ord.core.word.model.WordMapper
import com.backend.ord.core.word.service.WordService
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.domain.persistence.mappers.BankMapper
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.extensions.convertToSetExplicitly
import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import com.backend.ord.services.BankService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/words")
class WordController(
    private val jwtService: JwtService,
    private val bankService: BankService,
    private val wordMapper: WordMapper,
    private val userMapper: UserMapper,
    private val wordService: WordService,
    private val bankMapper: BankMapper
) {
    @PostMapping("/get-many-words")
    fun getAllWords(
        request: HttpServletRequest,
        @RequestBody @Valid requestBody: GetManyWordsRequest,
    ): ResponseEntity<PaginatedDataResponse<WordListItem>> {
        val user = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        return ResponseEntity.status(HttpStatus.OK).body(
            wordService.findManyWords(
                language = requestBody.language,
                wordType = requestBody.wordType,
                completed = requestBody.completed,
                wordExtraMark = requestBody.wordExtraMark,
                bookmarked = requestBody.bookmarked,
                searchingPhrase = requestBody.searchingPhrase,

                banksIds = requestBody.banksIds?.convertToSetExplicitly(paramName = "banksIds"),
                bankGroupsIds = requestBody.bankGroupsIds?.convertToSetExplicitly(paramName = "bankGroupsIds"),

                sortDirection = requestBody.sortDirection,
                sortBy = requestBody.sortBy,

                user = user,

                page = requestBody.page ?: 0,
                perPage = requestBody.perPage ?: 10
            )
        )
    }

    @GetMapping("/{id}")
    fun getWord(
        request: HttpServletRequest,
        @PathVariable id: UUID
    ): ResponseEntity<SingleWordResponse> {
        val user = jwtService.getAuthenticatedUser(request)!!

        return ResponseEntity.status(HttpStatus.OK).body(
            wordService.findOneWord(
                wordId = id,
                user = user
            )
        )
    }


    @PostMapping("/")
    fun createWord(
        request: HttpServletRequest,
        @Valid @RequestBody body: CreateWordRequest
    ): ResponseEntity<WordDTO> {
        val user: UserEntity = jwtService.getAuthenticatedUser(request)!!

        val bank = getBankFromRequestOrNull(
            bankId = body.bankId,
            bankToCreate = body.bankToCreate,
            user = user
        )

        val wordToSave = WordDTO(
            origin = body.origin,
            translatedTo = body.translatedTo ?: user.nativeLanguage,
            translatedFrom = body.translatedFrom,
            type = body.type,
            exampleSentences = body.exampleSentences,
            translation = body.translation,
            extraMark = body.extraMark,
            definition = body.definition,
            useCases = body.useCases,

            user = userMapper.toDTO(user),
            bank = bankMapper.toDTOOrNull(bank)
        )

        val result = wordService.save(wordMapper.toEntity(wordToSave))

        return ResponseEntity.status(HttpStatus.CREATED).body(wordMapper.toDTO(result))
    }

    @PatchMapping("/{id}")
    fun updateWord(
        request: HttpServletRequest,
        @PathVariable id: UUID,
        @Valid @RequestBody body: UpdateWordRequest
    ): ResponseEntity<WordDTO> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val currentWord = wordService.findByIdOrFail(id = id, userId = user.id)

        val bank = getBankFromRequestOrNull(
            bankId = body.bankId,
            bankToCreate = body.bankToCreate,
            user = user
        )

        val result: WordEntity = wordService.save(
            wordMapper.toEntity(
                WordDTO(
                    id = id,
                    origin = body.origin ?: currentWord.origin,
                    translatedTo = body.translatedTo ?: currentWord.translatedTo,
                    translatedFrom = body.translatedFrom ?: currentWord.translatedFrom,
                    type = body.type ?: currentWord.type,
                    exampleSentences = body.exampleSentences ?: currentWord.exampleSentences,
                    translation = body.translation ?: currentWord.translation,
                    extraMark = body.extraMark ?: currentWord.extraMark,
                    definition = body.definition ?: currentWord.definition,
                    useCases = body.useCases ?: currentWord.useCases,

                    user = userMapper.toDTO(user),
                    bank = bankMapper.toDTOOrNull(bank)
                )
            )
        )

        return ResponseEntity.status(HttpStatus.OK).body(wordMapper.toDTO(result))
    }

    @PostMapping("/{id}/change-bank")
    fun changeWordBank(
        request: HttpServletRequest,
        @PathVariable id: UUID,
        @Valid @RequestBody body: ChangeBankForSingleWordRequest
    ): ResponseEntity<Unit> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val bank = getBankFromRequestOrNull(
            user = user,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate
        )

        wordService.changeBankForSingleWord(
            wordId = id,
            bankId = bank?.id,
            userId = user.id
        )

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/change-bank-for-multiple-words")
    fun changeBankForMultipleWords(
        request: HttpServletRequest,
        @Valid @RequestBody body: ChangeBankForMultipleWordsRequest
    ): ResponseEntity<Unit> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val bank = getBankFromRequest(
            user = user,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate
        )

        wordService.changeBankForMultipleWords(
            wordIds = body.wordIds,
            bankId = bank.id,
            userId = user.id
        )

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/{id}/toggle-property")
    fun togglePropertyForOneWord(
        request: HttpServletRequest,
        @PathVariable id: UUID,
        @RequestParam(required = false) property: WordToggleableProperty
    ): ResponseEntity<Unit> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        wordService.toggleProperty(
            wordId = id,
            userId = user.id,
            property = property
        )

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/toggle-property-for-multiple-words")
    fun togglePropertyForManyWords(
        request: HttpServletRequest,
        @RequestParam(required = false) property: WordToggleableProperty,
        @Valid @RequestBody body: WordBulkActionRequest
    ): ResponseEntity<Unit> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        wordService.togglePropertyForManyWords(
            wordIds = body.ids.convertToSetExplicitly(paramName = "ids"),
            userId = user.id,
            property = property
        )

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @DeleteMapping("/{id}")
    fun deleteWord(
        request: HttpServletRequest,
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        val user = jwtService.getAuthenticatedUser(request)!!

        wordService.deleteById(
            id = id,
            userId = user.id
        )

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    private fun getBankFromRequest(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: UserEntity
    ): Bank {
        if (bankToCreate == null && bankId == null) {
            throw BadRequestException("Either bankToCreate or bankId has to be specifed")
        }

        if (bankToCreate != null && bankId != null) {
            throw BadRequestException("You cannot create a new bank and use an existing bank at the same time")
        }

        return try {
            bankService.findByIdOrCreate(
                bankId = bankId,
                bankToCreate = bankToCreate,
                user = user
            )!!
        } catch (_: DataIntegrityViolationException) {
            throw BadRequestException("The bank with name ${bankToCreate!!.name} already exists for this user")
        }
    }

    private fun getBankFromRequestOrNull(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: UserEntity
    ): Bank? {
        if (bankToCreate != null && bankId != null) {
            throw BadRequestException("You cannot create a new bank and use an existing bank at the same time")
        }

        return try {
            bankService.findByIdOrCreate(
                bankId = bankId,
                bankToCreate = bankToCreate,
                user = user
            )
        } catch (e: DataIntegrityViolationException) {
            throw BadRequestException("The bank with name ${bankToCreate!!.name} already exists for this user")
        }
    }
}