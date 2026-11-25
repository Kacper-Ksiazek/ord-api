package com.ord.features.ai_explainer.services.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import com.ord.features.ai_explainer.models.explanation.AIExplanationDTO
import com.ord.features.ai_explainer.models.explanation.AIExplanationMapper
import com.ord.features.ai_explainer.repositories.AIExplanationRepository
import com.ord.features.ai_explainer.services.AIExplanationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class AIExplanationServiceImpl(
    private val aiExplanationRepository: AIExplanationRepository,
    private val aiExplanationMapper: AIExplanationMapper
) : AIExplanationService {
    override val repository: AIExplanationRepository = aiExplanationRepository

    override fun create(
        id: UUID,
        userId: UUID,
        phrase: String,
        language: LanguageName,
        explanation: String
    ): Mono<Void> {
        return aiExplanationRepository
            .insertIfAbsent(
                id = id,
                userId = userId,
                phrase = phrase,
                language = language,
                explanation = explanation
            )
            .then()
    }

    override fun updateFollowUp(
        id: UUID,
        userId: UUID,
        action: ExplainPhraseFollowUpAction,
        text: String
    ): Mono<Void> {
        return aiExplanationRepository
            .updateFollowUpText(
                id = id,
                userId = userId,
                action = action,
                text = text
            )
            .then()
    }

    override fun updateClassification(
        id: UUID,
        userId: UUID,
        kind: ExplainPhraseKind,
        emoji: String?
    ): Mono<Void> {
        return aiExplanationRepository
            .updateClassification(id = id, userId = userId, kind = kind, emoji = emoji)
            .then()
    }

    override fun getById(
        id: UUID,
        userId: UUID
    ): Mono<AIExplanationDTO> {
        return findByIdOrFail(id = id, userId = userId, message = "Explanation not found")
            .map { aiExplanationMapper.toDTO(it) }
    }

    override fun findRecent(userId: UUID): Flux<AIExplanationDTO> {
        return aiExplanationRepository
            .findTop20ByUserIdOrderByCreatedAtDesc(userId)
            .map { aiExplanationMapper.toDTO(it) }
    }

    override fun delete(
        id: UUID,
        userId: UUID
    ): Mono<Void> {
        return aiExplanationRepository.deleteByIdAndUserId(id = id, userId = userId)
    }
}
