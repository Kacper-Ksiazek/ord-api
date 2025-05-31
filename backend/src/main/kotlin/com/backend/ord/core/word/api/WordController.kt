package com.backend.ord.core.word.api

import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.core.auth.security.AuthenticatedUser
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.facades.WordCRUDFacade
import com.backend.ord.core.word.api.requests.dto.*
import com.backend.ord.core.word.api.requests.enums.WordToggleableProperty
import com.backend.ord.core.word.api.responses.dto.SingleWordResponse
import com.backend.ord.core.word.api.responses.dto.WordListItem
import com.backend.ord.core.word.model.WordDTO
import com.backend.ord.core.word.service.WordService
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.extensions.convertToSetExplicitly
import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import com.backend.ord.services.BankService
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/words")
class WordController(
    private val bankService: BankService,
    private val wordService: WordService,
    // ---
    private val wordCRUDFacade: WordCRUDFacade
) {
    @PostMapping("/get-many-words")
    fun getAllWords(
        @RequestBody @Valid requestBody: GetManyWordsRequest,
        @AuthenticatedUser user: UserEntity
    ): ResponseEntity<PaginatedDataResponse<WordListItem>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            wordCRUDFacade.getManyWords(requestBody, user)
        )
    }

    @GetMapping("/{id}")
    fun getWord(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity
    ): ResponseEntity<SingleWordResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(
            wordCRUDFacade.getSingleWord(id, user)
        )
    }


    @PostMapping("/")
    fun createWord(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: CreateWordRequest
    ): ResponseEntity<WordDTO> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(wordCRUDFacade.createWord(body, user))
    }

    @PatchMapping("/{id}")
    fun updateWord(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: UpdateWordRequest
    ): ResponseEntity<WordDTO> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(wordCRUDFacade.updateWord(id, body, user))
    }


    @DeleteMapping("/{id}")
    fun deleteWord(
        @AuthenticatedUser user: UserEntity,
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        wordCRUDFacade.deleteWord(id, user)

        return ResponseEntity.status(HttpStatus.OK).build()
    }


    @PostMapping("/{id}/change-bank")
    fun changeWordBank(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: ChangeBankForSingleWordRequest
    ): ResponseEntity<Unit> {
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
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: ChangeBankForMultipleWordsRequest
    ): ResponseEntity<Unit> {
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
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity,
        @RequestParam(required = false) property: WordToggleableProperty
    ): ResponseEntity<Unit> {
        wordService.toggleProperty(
            wordId = id,
            userId = user.id,
            property = property
        )

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/toggle-property-for-multiple-words")
    fun togglePropertyForManyWords(
        @RequestParam(required = false) property: WordToggleableProperty,
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: WordBulkActionRequest
    ): ResponseEntity<Unit> {
        wordService.togglePropertyForManyWords(
            wordIds = body.ids.convertToSetExplicitly(paramName = "ids"),
            userId = user.id,
            property = property
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