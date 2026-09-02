package com.ord.core.word.services

import com.ord.config.GamesConfig
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word_progress.WordProgressEntity
import com.ord.core.word.repositories.WordProgressWithWord
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.services.UserResourceService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

interface WordProgressService : UserResourceService<WordProgressEntity> {
    fun createInitialProgress(wordId: UUID, userId: UUID): Mono<WordProgressEntity>

    fun findByWordId(wordId: UUID, userId: UUID): Mono<WordProgressEntity>

    fun findAllByOrigins(
        origins: Set<String>,
        language: LanguageName,
        userId: UUID,
    ): Flux<WordProgressWithWord>

    fun applyScoreDelta(
        progress: WordProgressEntity,
        delta: Int,
        now: Instant = Instant.now(),
    ): WordProgressEntity

    fun countCompleted(language: LanguageName, userId: UUID): Mono<CountingSummary>
}

fun WordProgressService.applyAnswerScore(
    progress: WordProgressEntity,
    score: WordAnswerScore,
    now: Instant = Instant.now(),
): WordProgressEntity = applyScoreDelta(progress, score.dbPoints, now)

fun WordProgressEntity.isCompleted(): Boolean =
    points >= GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD
