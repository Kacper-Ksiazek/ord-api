package com.ord.features.quickly_added_words.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface QAWRepositoryCustomMethods {
    fun findManyQAWs(
        userId: UUID,
        page: Int? = 1,
        perPage: Int? = 50
    ): Mono<PaginatedDataResponse<QuicklyAddedWordEntity>>

    fun approveManyByIdsAndUserId(
        ids: Set<UUID>,
        userId: UUID
    ): Mono<Unit>

    fun findAllWordsByUserIdAndLanguage(
        userId: UUID,
        language: LanguageName
    ): Flux<String>
}