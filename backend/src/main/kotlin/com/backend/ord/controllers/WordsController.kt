package com.backend.ord.controllers

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.data.*
import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.api.responses.words.SingleWordResponse
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.domain.mappers.BankMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.WordMapper
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.extensions.convertToSetExplicitly
import com.backend.ord.services.BankService
import com.backend.ord.services.WordService
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
        @RequestBody @Valid requestBody: GetManyWordsRequestData,
    ): ResponseEntity<PaginatedDataResponse<WordAsGetManyWordResponse>> {
        val user = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        return ResponseEntity.status(HttpStatus.OK).body(
            wordService.findManyWords(
                language = requestBody.language,
                wordType = requestBody.wordType,
                completed = requestBody.completed,
                wordExtraMark = requestBody.wordExtraMark,
                bookmarkedOnly = requestBody.bookmarkedOnly,
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