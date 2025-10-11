package com.ord.features.quickly_added_words.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.quickly_added_words.api.facades.QAWFacade
import com.ord.features.quickly_added_words.api.requests.CreateQAWRequest
import com.ord.features.quickly_added_words.api.requests.UpdateQAWRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/quickly-added-words")
class QuicklyAddedWordsController(
    private val qawFacade: QAWFacade,
) {
    // -------
    // CREATE
    // -------

    @PostMapping("/")
    fun createOne(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateQAWRequest
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>> = qawFacade.createOne(
        user = user.id,
        body = body
    )

    @PostMapping("/bulk-create")
    fun bulkCreate(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: List<CreateQAWRequest>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>> = qawFacade.bulkCreate(
        user = user.id,
        body = body
    )

    // -------
    // READ
    // -------

    @GetMapping("/")
    fun getManyQAWs(
        @AuthenticatedUser user: UserDTO,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) perPage: Int?
    ): Mono<ResponseEntity<PaginatedDataResponse<QuicklyAddedWordDTO>>> = qawFacade.getManyQAWs(
        user = user.id,
        page = page,
        perPage = perPage
    )

    // -------
    // UPDATE
    // -------

    @PatchMapping("/{id}")
    fun updateOne(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: UpdateQAWRequest
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>> = qawFacade.updateOne(
        user = user.id,
        qawId = id,
        newWord = body.updatedWord
    )

    @PatchMapping("/bulk-update")
    fun bulkUpdate(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: Map<UUID, String>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>> = qawFacade.bulkUpdate(
        user = user.id,
        body = body.map { (id, word) -> Pair(id, word) }
    )

    // -------
    // DELETE
    // -------

    @DeleteMapping("/{id}")
    fun deleteOne(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserDTO
    ): Mono<ResponseEntity<Unit>> = qawFacade.deleteOne(
        user = user.id,
        qawId = id
    )

    @DeleteMapping("/bulk-delete")
    fun bulkDelete(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: List<UUID>
    ): Mono<ResponseEntity<Unit>> = qawFacade.bulkDelete(
        user = user.id,
        body = body
    )
}