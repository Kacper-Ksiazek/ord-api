package com.ord.core.word.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.facades.WordBankManagementFacade
import com.ord.core.word.api.facades.WordCRUDFacade
import com.ord.core.word.api.facades.WordPropertyToggleFacade
import com.ord.core.word.api.requests.dto.*
import com.ord.core.word.api.requests.enums.WordToggleableProperty
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
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
    ): Mono<ResponseEntity<PaginatedDataResponse<WordListItem>>> {
        return wordCRUDFacade.getManyWords(requestBody, user)
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

    @GetMapping("/{id}")
    fun getWord(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity
    ): Mono<ResponseEntity<SingleWordResponse>> {
        return wordCRUDFacade.getSingleWord(id, user)
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }


    @PostMapping("/")
    fun createWord(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: CreateWordRequest
    ): Mono<ResponseEntity<WordDTO>> {
        return wordCRUDFacade.createWord(body, user)
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @PatchMapping("/{id}")
    fun updateWord(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: UpdateWordRequest
    ): Mono<ResponseEntity<WordDTO>> {
        return wordCRUDFacade.updateWord(id, body, user)
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }


    @DeleteMapping("/{id}")
    fun deleteWord(
        @AuthenticatedUser user: UserEntity,
        @PathVariable id: UUID
    ): Mono<ResponseEntity<Unit>> {
        return wordCRUDFacade.deleteWord(id, user)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }

    // -------
    // Change Bank
    // -------

    @PostMapping("/{id}/change-bank")
    fun changeWordBank(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: ChangeBankForSingleWordRequest
    ): Mono<ResponseEntity<Unit>> {
        return wordBankManagementFacade.changeBankOfOneWord(id, body, user)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }

    @PostMapping("/change-bank-for-multiple-words")
    fun changeBankForMultipleWords(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: ChangeBankForMultipleWordsRequest
    ): Mono<ResponseEntity<Unit>> {
        return wordBankManagementFacade.changeBankOfMultipleWords(body, user)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }

    // -------
    // Toggle properties
    // -------

    @PostMapping("/{id}/toggle-property")
    fun togglePropertyForOneWord(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserEntity,
        @RequestParam(required = false) property: WordToggleableProperty
    ): Mono<ResponseEntity<Unit>> {
        return wordPropertyToggleFacade.togglePropertyForOneWord(id, property, user)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }

    @PostMapping("/toggle-property-for-multiple-words")
    fun togglePropertyForManyWords(
        @RequestParam(required = false) property: WordToggleableProperty,
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: WordBulkActionRequest
    ): Mono<ResponseEntity<Unit>> {
        return wordPropertyToggleFacade.togglePropertyForMultipleWords(body, property, user)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }
}