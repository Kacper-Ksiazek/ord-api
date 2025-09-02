package com.ord.core.word.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.dto.CreateWordRequest
import com.ord.core.word.api.requests.dto.GetManyWordsRequest
import com.ord.core.word.api.requests.dto.UpdateWordRequest
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import reactor.core.publisher.Mono
import java.util.*

interface WordCRUDFacade {
    fun getManyWords(
        requestBody: GetManyWordsRequest,
        user: UserEntity
    ): Mono<PaginatedDataResponse<WordListItem>>

    fun getSingleWord(
        id: UUID,
        user: UserEntity
    ): Mono<SingleWordResponse>

    fun createWord(
        body: CreateWordRequest,
        user: UserEntity
    ): Mono<WordDTO>

    fun updateWord(
        id: UUID,
        body: UpdateWordRequest,
        user: UserEntity
    ): Mono<WordDTO>

    fun deleteWord(
        id: UUID,
        user: UserEntity
    ): Mono<Void>
}