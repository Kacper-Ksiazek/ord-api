package com.ord.core.word.repositories

import com.ord.core.word.models.word_details.WordDetailsEntity
import com.ord.shared.repositories.UserResourceRepository
import reactor.core.publisher.Mono
import java.util.*

interface WordDetailsRepository : UserResourceRepository<WordDetailsEntity> {
    fun findByWordIdAndUserId(
        wordId: UUID,
        userId: UUID
    ): Mono<WordDetailsEntity>

    fun existsByWordIdAndUserId(
        wordId: UUID,
        userId: UUID
    ): Mono<Boolean>

    fun deleteByWordIdAndUserId(
        wordId: UUID,
        userId: UUID
    ): Mono<Void>
}