package com.backend.ord.core.word.api

import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.core.auth.security.AuthenticatedUser
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.facades.WordBankManagementFacade
import com.backend.ord.core.word.api.facades.WordCRUDFacade
import com.backend.ord.core.word.api.facades.WordPropertyToggleFacade
import com.backend.ord.core.word.api.requests.dto.*
import com.backend.ord.core.word.api.requests.enums.WordToggleableProperty
import com.backend.ord.core.word.api.responses.dto.SingleWordResponse
import com.backend.ord.core.word.api.responses.dto.WordListItem
import com.backend.ord.core.word.model.WordDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/words")
class WordController(
    private val wordCRUDFacade: WordCRUDFacade,
    private val wordPropertyToggleFacade: WordPropertyToggleFacade,
    private val wordBankManagementFacade: WordBankManagementFacade,
) {
    // -------
    // CRUD
    // -------

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

    // -------
    // Change Bank
    // -------

    @PostMapping("/{id}/change-bank")
    fun changeWordBank(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: ChangeBankForSingleWordRequest
    ): ResponseEntity<Unit> {
        wordBankManagementFacade.changeBankOfOneWord(id, body, user)

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/change-bank-for-multiple-words")
    fun changeBankForMultipleWords(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: ChangeBankForMultipleWordsRequest
    ): ResponseEntity<Unit> {
        wordBankManagementFacade.changeBankOfMultipleWords(body, user)

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    // -------
    // Toggle properties
    // -------

    @PostMapping("/{id}/toggle-property")
    fun togglePropertyForOneWord(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity,
        @RequestParam(required = false) property: WordToggleableProperty
    ): ResponseEntity<Unit> {
        wordPropertyToggleFacade.togglePropertyForOneWord(id, property, user)

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/toggle-property-for-multiple-words")
    fun togglePropertyForManyWords(
        @RequestParam(required = false) property: WordToggleableProperty,
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: WordBulkActionRequest
    ): ResponseEntity<Unit> {
        wordPropertyToggleFacade.togglePropertyForMultipleWords(body, property, user)

        return ResponseEntity.status(HttpStatus.OK).build()
    }
}