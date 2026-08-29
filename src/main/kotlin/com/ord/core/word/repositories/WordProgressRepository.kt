package com.ord.core.word.repositories

import com.ord.core.word.models.word_progress.WordProgressEntity
import com.ord.shared.repositories.UserResourceRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface WordProgressRepository :
    UserResourceRepository<WordProgressEntity>,
    WordProgressRepositoryCustomMethods {
    fun findByWordIdAndUserId(wordId: UUID, userId: UUID): Mono<WordProgressEntity>

    fun findAllByWordIdInAndUserId(wordIds: Set<UUID>, userId: UUID): Flux<WordProgressEntity>
}
