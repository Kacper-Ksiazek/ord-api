package com.ord.core.word.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word_progress.WordProgressEntity
import com.ord.shared.domain.dto.CountingSummary
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface WordProgressRepositoryCustomMethods {
    fun findAllByOriginsAndUserId(
        origins: Set<String>,
        language: LanguageName,
        userId: UUID,
    ): Flux<WordProgressWithWord>

    fun countCompleted(
        language: LanguageName,
        userId: UUID,
    ): Mono<CountingSummary>
}

data class WordProgressWithWord(
    val progress: WordProgressEntity,
    val sourceWord: String,
)
