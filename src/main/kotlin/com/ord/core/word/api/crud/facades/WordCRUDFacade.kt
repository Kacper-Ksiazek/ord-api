package com.ord.core.word.api.crud.facades

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.crud.requests.dto.CreateWordRequest
import com.ord.core.word.api.crud.requests.dto.GetManyWordsRequest
import com.ord.core.word.api.crud.requests.dto.UpdateWordRequest
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.api.crud.responses.dto.WordOverviewResponse
import com.ord.core.word.api.crud.responses.dto.WordsPaginatedDataResponse
import com.ord.core.word.models.word.WordDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.*

interface WordCRUDFacade {
    fun listWords(
        userId: UUID,
        language: LanguageName,
        page: Int?,
        perPage: Int?,
    ): Mono<ResponseEntity<WordsPaginatedDataResponse>>

    fun searchWords(
        requestBody: GetManyWordsRequest,
        userId: UUID,
    ): Mono<ResponseEntity<WordsPaginatedDataResponse>>

    fun getOverview(
        userId: UUID,
        language: LanguageName?,
    ): Mono<ResponseEntity<WordOverviewResponse>>

    fun getSingleWord(
        id: UUID,
        userId: UUID,
    ): Mono<ResponseEntity<SingleWordResponse>>

    fun createWord(
        body: CreateWordRequest,
        user: UserDTO,
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
