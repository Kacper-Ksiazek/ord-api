package com.ord.features.quickly_added_words.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.quickly_added_words.api.facades.QAWFacade
import com.ord.features.quickly_added_words.api.requests.ApproveManyQAWRequest
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
        userId = user.id,
        body = body
    )

    @PostMapping("/bulk-create")
    fun bulkCreate(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: List<CreateQAWRequest>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>> = qawFacade.bulkCreate(
        userId = user.id,
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
        userId = user.id,
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
        userId = user.id,
        qawId = id,
        body = body
    )

    @PatchMapping("/bulk-update")
    fun bulkUpdate(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: Map<UUID, String>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>> = qawFacade.bulkUpdate(
        userId = user.id,
        body = body.map { (id, word) -> Pair(id, word) }
    )

    @PatchMapping("/approve-many")
    fun approveMany(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ApproveManyQAWRequest
    ): Mono<ResponseEntity<Unit>> = qawFacade.approveMany(
        userId = user.id,
        body = body
    )

    // -------
    // DELETE
    // -------

    @DeleteMapping("/{id}")
    fun deleteOne(
        @PathVariable id: UUID,
        @AuthenticatedUser user: UserDTO
    ): Mono<ResponseEntity<Unit>> = qawFacade.deleteOne(
        userId = user.id,
        qawId = id
    )

    @PostMapping("/bulk-delete")
    fun bulkDelete(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: List<UUID>
    ): Mono<ResponseEntity<Unit>> = qawFacade.bulkDelete(
        userId = user.id,
        body = body
    )
}