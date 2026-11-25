package com.ord.features.ai_explainer.services

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import com.ord.features.ai_explainer.models.explanation.AIExplanationDTO
import com.ord.features.ai_explainer.models.explanation.AIExplanationEntity
import com.ord.shared.services.UserResourceService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface AIExplanationService : UserResourceService<AIExplanationEntity> {
    /** Called by the API once the explain stream has finished. */
    fun create(
        id: UUID,
        userId: UUID,
        phrase: String,
        language: LanguageName,
        explanation: String
    ): Mono<Void>

    /** Called by the API once a follow-up stream has finished; stores its text on the existing entry. */
    fun updateFollowUp(
        id: UUID,
        userId: UUID,
        action: ExplainPhraseFollowUpAction,
        text: String
    ): Mono<Void>

    /** Called by the API after the suggest-actions classification; no-op when the entry is missing. */
    fun updateClassification(
        id: UUID,
        userId: UUID,
        kind: ExplainPhraseKind,
        emoji: String?
    ): Mono<Void>

    /** Called from the frontend; one saved explanation, or a 404 when it is missing or not the user's. */
    fun getById(
        id: UUID,
        userId: UUID
    ): Mono<AIExplanationDTO>

    /** Called from the frontend; the user's latest saved explanations, newest first. */
    fun findRecent(userId: UUID): Flux<AIExplanationDTO>

    /** Called from the frontend; idempotent and scoped to the user. */
    fun delete(
        id: UUID,
        userId: UUID
    ): Mono<Void>
}
