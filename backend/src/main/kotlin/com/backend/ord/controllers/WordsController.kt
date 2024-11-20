package com.backend.ord.controllers

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.data.ChangeBankForMultipleWordsRequestData
import com.backend.ord.api.requests.word.data.ChangeBankForSingleWordRequestData
import com.backend.ord.api.requests.word.data.CreateWordRequestData
import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.domain.mappers.BankMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.WordMapper
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.services.BankService
import com.backend.ord.services.WordService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
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
    @GetMapping("/")
    fun getAllWords(
        request: HttpServletRequest,

        // TODO: 3. Implement sorting

        // TODO: 4. Implement phrase search

        // TODO: 5. Return the total amount of pages for the given pagination parameters

        @RequestParam(required = true) language: LanguageName,

        @RequestParam(required = false) @Min(0) page: Int = 0,
        @RequestParam(required = false) @Min(10) @Max(500) perPage: Int = 10,

        @RequestParam(required = false) wordType: WordType?,
        @RequestParam(required = false) searchingPhrase: String?,
        @RequestParam(required = false) wordExtraMark: WordExtraMark?,
        @RequestParam(required = false) bookmarkedOnly: Boolean? = false,

        @RequestParam(required = false) banksIds: List<UUID>?,
        @RequestParam(required = false) bankGroupsIds: List<UUID>?,

        @RequestParam(required = false) sortDirection: SortDirection? = SortDirection.DESC,
        @RequestParam(required = false) sortBy: GetAllWordsSortOptions? = GetAllWordsSortOptions.CREATED_AT
    ): ResponseEntity<PaginatedDataResponse<WordAsGetManyWordResponse>> {
        val user = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        return ResponseEntity.status(HttpStatus.OK).body(
            wordService.findManyWords(
                language = language,
                wordType = wordType,
                wordExtraMark = wordExtraMark,
                bookmarkedOnly = bookmarkedOnly,
                searchingPhrase = searchingPhrase,

                banksIds = banksIds,
                bankGroupsIds = bankGroupsIds,

                sortDirection = sortDirection,
                sortBy = sortBy,

                user = user,

                page = page,
                perPage = perPage
            )
        )
    }

    @PostMapping("/")
    fun createWord(
        request: HttpServletRequest,
        @Valid @RequestBody body: CreateWordRequestData
    ): ResponseEntity<WordDTO> {
        val user: User = jwtService.getAuthenticatedUser(request)!!

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

        return ResponseEntity.status(HttpStatus.CREATED).body(wordMapper.toDTO(result));
    }

    @PatchMapping("/{id}")
    fun updateWord(
        request: HttpServletRequest,
        @PathVariable id: UUID,
        @Valid @RequestBody body: UpdateWordRequestData
    ): ResponseEntity<WordDTO> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val currentWord = wordService.findByIdOrFail(id = id, userId = user.id)

        val bank = getBankFromRequestOrNull(
            bankId = body.bankId,
            bankToCreate = body.bankToCreate,
            user = user
        )

        val result: Word = wordService.save(
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
        @Valid @RequestBody body: ChangeBankForSingleWordRequestData
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
        @Valid @RequestBody body: ChangeBankForMultipleWordsRequestData
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
        bankToCreate: CreateBankRequestData?,
        user: User
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
        } catch (e: DataIntegrityViolationException) {
            throw BadRequestException("The bank with name ${bankToCreate!!.name} already exists for this user")
        }
    }

    private fun getBankFromRequestOrNull(
        bankId: UUID?,
        bankToCreate: CreateBankRequestData?,
        user: User
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