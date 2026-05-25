package com.ord.features.quickly_added_words.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface QAWRepositoryCustomMethods {
    fun countByApprovalStatus(userId: UUID): Mono<QAWApprovalCounts>

    fun findManyQAWs(
        userId: UUID,
        page: Int? = 1,
        perPage: Int? = 50,
        isApproved: Boolean? = null,
    ): Mono<QAWPaginatedResult>

    fun approveManyByIdsAndUserId(
        ids: Set<UUID>,
        userId: UUID
    ): Mono<Unit>

    fun findAllWordsByUserIdAndLanguage(
        userId: UUID,
        language: LanguageName
    ): Flux<String>
}