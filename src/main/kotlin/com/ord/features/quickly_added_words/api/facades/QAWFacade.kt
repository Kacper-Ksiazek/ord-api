package com.ord.features.quickly_added_words.api.facades

import com.ord.features.quickly_added_words.api.requests.CreateQAWRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface QAWFacade {
    /*
     * CREATE
     */

    fun createOne(
        user: UUID,
        body: CreateQAWRequest,
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>>


    fun bulkCreate(
        user: UUID,
        body: List<CreateQAWRequest>,
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>>

    /*
     * READ
     */

    fun getManyQAWs(
        user: UUID,
        page: Int? = 1,
        perPage: Int? = 50
    ): Mono<ResponseEntity<PaginatedDataResponse<QuicklyAddedWordDTO>>>


    /*
     * UPDATE
     */

    fun updateOne(
        user: UUID,
        qawId: UUID,
        newWord: String
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>>


    fun bulkUpdate(
        user: UUID,
        body: List<Pair<UUID, String>>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>>


    /*
     * DELETE
     */

    fun deleteOne(
        user: UUID,
        qawId: UUID
    ): Mono<ResponseEntity<Unit>>


    fun bulkDelete(
        user: UUID,
        body: List<UUID>
    ): Mono<ResponseEntity<Unit>>
}