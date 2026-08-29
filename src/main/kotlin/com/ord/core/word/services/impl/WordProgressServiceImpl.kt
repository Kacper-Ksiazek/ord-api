package com.ord.core.word.services.impl

import com.ord.config.GamesConfig
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word_progress.WordProgressEntity
import com.ord.core.word.models.word_progress.WordProgressMapper
import com.ord.core.word.repositories.WordProgressRepository
import com.ord.core.word.repositories.WordProgressWithWord
import com.ord.core.word.services.WordProgressService
import com.ord.exceptions.REST.NotFoundException
import com.ord.shared.domain.dto.CountingSummary
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Service
class WordProgressServiceImpl(
    private val wordProgressRepository: WordProgressRepository,
    private val wordProgressMapper: WordProgressMapper,
) : WordProgressService {
    override val repository: WordProgressRepository = wordProgressRepository

    override fun createInitialProgress(wordId: UUID, userId: UUID): Mono<WordProgressEntity> {
        return wordProgressRepository.save(wordProgressMapper.toInitialEntity(wordId, userId))
    }

    override fun findByWordId(wordId: UUID, userId: UUID): Mono<WordProgressEntity> {
        return wordProgressRepository.findByWordIdAndUserId(wordId, userId)
            .switchIfEmpty(Mono.error(NotFoundException("Progress for word $wordId not found")))
    }

    override fun findAllByOrigins(
        origins: Set<String>,
        language: LanguageName,
        userId: UUID,
    ): Flux<WordProgressWithWord> {
        return wordProgressRepository.findAllByOriginsAndUserId(
            origins = origins,
            language = language,
            userId = userId,
        )
    }

    override fun applyScoreDelta(
        progress: WordProgressEntity,
        delta: Int,
        now: Instant,
    ): WordProgressEntity {
        val wasCompleted = progress.points >= GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD
        progress.points = maxOf(0, progress.points + delta)
        val isCompletedNow = progress.points >= GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD

        when {
            isCompletedNow && !wasCompleted -> {
                progress.completedAt = now
                if (progress.firstCompletedAt == null) {
                    progress.firstCompletedAt = now
                }
            }

            !isCompletedNow && wasCompleted -> {
                progress.completedAt = null
            }
        }

        progress.updatedAt = now
        return progress
    }

    override fun countCompleted(language: LanguageName, userId: UUID): Mono<CountingSummary> {
        return wordProgressRepository.countCompleted(language, userId)
    }
}
