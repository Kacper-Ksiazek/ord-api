package com.ord.features.ai_explainer.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import com.ord.features.ai_explainer.models.explanation.AIExplanationEntity
import com.ord.shared.repositories.UserResourceRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface AIExplanationRepositoryCustomMethods {
    /** Inserts an explanation under the caller-supplied id; does nothing when the id is already taken. */
    fun insertIfAbsent(
        id: UUID,
        userId: UUID,
        phrase: String,
        language: LanguageName,
        explanation: String
    ): Mono<Long>

    /** Records what the explanation was about and its emoji; returns the number of updated rows. */
    fun updateClassification(
        id: UUID,
        userId: UUID,
        kind: ExplainPhraseKind,
        emoji: String?
    ): Mono<Long>

    /** Writes the follow-up text into the column matching [action]; returns the number of updated rows. */
    fun updateFollowUpText(
        id: UUID,
        userId: UUID,
        action: ExplainPhraseFollowUpAction,
        text: String
    ): Mono<Long>
}

interface AIExplanationRepository :
    UserResourceRepository<AIExplanationEntity>,
    AIExplanationRepositoryCustomMethods {
    fun findTop20ByUserIdOrderByCreatedAtDesc(userId: UUID): Flux<AIExplanationEntity>
}
