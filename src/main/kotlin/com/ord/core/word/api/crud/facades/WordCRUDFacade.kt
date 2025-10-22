package com.ord.core.word.api.crud.facades

import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.core.word.api.crud.requests.dto.GetManyWordsRequest
import com.ord.core.word.api.crud.requests.dto.UpdateWordRequest
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.api.crud.responses.dto.WordListItem
import com.ord.core.word.models.word.WordDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.*

interface WordCRUDFacade {
    fun getManyWords(
        requestBody: GetManyWordsRequest,
        userId: UUID
    ): Mono<ResponseEntity<PaginatedDataResponse<WordListItem>>>

    fun getSingleWord(
        id: UUID,
        userId: UUID,
    ): Mono<ResponseEntity<SingleWordResponse>>

    fun createWord(
        body: CreateWordRequest,
        user: UserDTO
    ): Mono<ResponseEntity<WordDTO>>

    fun updateWord(
        id: UUID,
        body: UpdateWordRequest,
        userId: UUID,
    ): Mono<ResponseEntity<WordDTO>>

    fun deleteWord(
        id: UUID,
        userId: UUID,
    ): Mono<ResponseEntity<Unit>>
}