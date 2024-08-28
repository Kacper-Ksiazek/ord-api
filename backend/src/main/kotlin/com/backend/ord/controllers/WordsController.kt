package com.backend.ord.controllers

import com.backend.ord.api.requests.bank.data.CreateBankRequest
import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.data.ChangeBankForMultipleWordsRequestData
import com.backend.ord.api.requests.word.data.ChangeBankForSingleWordRequestData
import com.backend.ord.api.requests.word.data.CreateWordRequestData
import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.domain.mappers.BankMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.WordMapper
import com.backend.ord.exceptions.REST.BadRequestException
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
    @GetMapping("/")
    fun getAllWords(
        request: HttpServletRequest,

        // TODO: Implement missing parameters
        @RequestParam(required = false) bankId: UUID?,
        @RequestParam(required = false) wordId: UUID?,
        @RequestParam(required = false) amount: Int?,

        // TODO: FInd better name for this parameter
        // @RequestParam(required = false) wordsRandomizationMethod: ENUM { FULL_RANDOM ( DEFAULT ), PROBLEMATIC_FIRST, LEAST_RECENTLY_USED, RECENTLY_ADDED )
    ): ResponseEntity<List<WordDTO>> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val words: List<WordDTO> = wordMapper.toDTOList(
            wordService.findAll(userId = user.id)
        )

        return ResponseEntity.ok(words)
    }

    @PostMapping("/")
    fun createWord(
        request: HttpServletRequest,
        @Valid @RequestBody body: CreateWordRequestData
    ): ResponseEntity<WordDTO> {
        val user: User = jwtService.getAuthenticatedUser(request)!!

        if (body.bankToCreate != null && body.bankId != null) {
            throw BadRequestException("You cannot create a new bank and use an existing bank at the same time")
        }

        val bank = getBankFromRequest(
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

        val bank = getBankFromRequest(
            bankId = body.bankId,
            bankToCreate = body.bankToCreate,
            user = user
        )

        val result: Word = wordService.save(
            wordMapper.toEntity(
                WordDTO(
                    id = id,
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
            )
        )

        return ResponseEntity.status(HttpStatus.OK).body(wordMapper.toDTO(result))
    }

    @PostMapping("/{id}/change-bank")
    fun changeWordBank(
        request: HttpServletRequest,
        @PathVariable id: UUID,
        @RequestBody body: ChangeBankForSingleWordRequestData
    ): ResponseEntity<WordDTO> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val result: Word = wordService.changeBankForSingleWord(
            wordId = id,
            bankId = body.bankId,
            userId = user.id
        )

        return ResponseEntity.status(HttpStatus.OK).body(wordMapper.toDTO(result))
    }

    @PostMapping("/change-bank-for-multiple-words")
    fun changeBankForMultipleWords(
        request: HttpServletRequest,
        @RequestBody body: ChangeBankForMultipleWordsRequestData
    ): ResponseEntity<List<WordDTO>> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val result: List<Word> = wordService.changeBankForMultipleWords(
            wordIds = body.wordIds,
            bankId = body.bankId,
            userId = user.id
        )

        return ResponseEntity.status(HttpStatus.OK).body(wordMapper.toDTOList(result))
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
    ): Bank? {
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