package com.ord.features.quickly_added_words.api.facades

import com.ord.features.quickly_added_words.api.requests.ApproveManyQAWRequest
import com.ord.features.quickly_added_words.api.requests.CreateQAWRequest
import com.ord.features.quickly_added_words.api.requests.UpdateQAWRequest
import com.ord.features.quickly_added_words.api.responses.QAWOverviewResponse
import com.ord.features.quickly_added_words.api.responses.QAWPaginatedDataResponse
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
        userId: UUID,
        body: CreateQAWRequest,
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>>


    fun bulkCreate(
        userId: UUID,
        body: List<CreateQAWRequest>,
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>>

    /*
     * READ
     */

    fun getManyQAWs(
        userId: UUID,
        page: Int? = 0,
        perPage: Int? = 50,
        isApproved: Boolean? = null,
    ): Mono<ResponseEntity<QAWPaginatedDataResponse>>

    fun getOverview(userId: UUID): Mono<ResponseEntity<QAWOverviewResponse>>


    /*
     * UPDATE
     */

    fun updateOne(
        userId: UUID,
        qawId: UUID,
        body: UpdateQAWRequest
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>>


    fun bulkUpdate(
        userId: UUID,
        body: List<Pair<UUID, String>>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>>


    fun approveMany(
        userId: UUID,
        body: ApproveManyQAWRequest
    ): Mono<ResponseEntity<Unit>>


    /*
     * DELETE
     */

    fun deleteOne(
        userId: UUID,
        qawId: UUID
    ): Mono<ResponseEntity<Unit>>


    fun bulkDelete(
        userId: UUID,
        body: List<UUID>
    ): Mono<ResponseEntity<Unit>>
}