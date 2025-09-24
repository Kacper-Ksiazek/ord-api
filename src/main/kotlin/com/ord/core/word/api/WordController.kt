package com.ord.core.word.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
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
        @AuthenticatedUser user: UserDTO,
    ): Mono<ResponseEntity<PaginatedDataResponse<WordListItem>>> = wordCRUDFacade.getManyWords(
        requestBody = requestBody,
        userId = user.id
    )


    @GetMapping("/{id}")
    fun getWord(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserDTO,
    ): Mono<ResponseEntity<SingleWordResponse>> = wordCRUDFacade.getSingleWord(
        id = id,
        userId = user.id
    )


    @PostMapping("/")
    fun createWord(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateWordRequest
    ): Mono<ResponseEntity<WordDTO>> {
        return wordCRUDFacade.createWord(body, user)
    }

    @PatchMapping("/{id}")
    fun updateWord(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: UpdateWordRequest
    ): Mono<ResponseEntity<WordDTO>> {
        return wordCRUDFacade.updateWord(
            id = id,
            body = body,
            userId = user.id
        )
    }


    @DeleteMapping("/{id}")
    fun deleteWord(
        @AuthenticatedUser user: UserDTO,
        @PathVariable id: UUID
    ): Mono<ResponseEntity<Unit>> = wordCRUDFacade.deleteWord(
        id = id,
        userId = user.id
    )


    // -------
    // Change Bank
    // -------

    @PostMapping("/{id}/change-bank")
    fun changeWordBank(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ChangeBankForSingleWordRequest
    ): Mono<ResponseEntity<Unit>> {
        return wordBankManagementFacade.changeBankOfOneWord(id, body, user)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build<Unit>() })
    }

    @PostMapping("/change-bank-for-multiple-words")
    fun changeBankForMultipleWords(
        @AuthenticatedUser user: UserDTO,
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
        @AuthenticatedUser user: UserDTO,
        @RequestParam(required = false) property: WordToggleableProperty
    ): Mono<ResponseEntity<Unit>> = wordPropertyToggleFacade.togglePropertyForOneWord(
        id = id,
        property = property,
        userId = user.id
    )


    @PostMapping("/toggle-property-for-multiple-words")
    fun togglePropertyForManyWords(
        @RequestParam(required = false) property: WordToggleableProperty,
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: WordBulkActionRequest
    ): Mono<ResponseEntity<Unit>> =
        wordPropertyToggleFacade.togglePropertyForMultipleWords(
            body = body,
            property = property,
            userId = user.id
        )
}