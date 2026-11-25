package com.ord.features.ai_explainer.models.explanation

import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.shared.models.mappers.UnidirectionalEntityMapper
import com.ord.shared.utils.OrdJsonMapper
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Component
import tools.jackson.core.type.TypeReference

@Component
class AIExplanationMapper : UnidirectionalEntityMapper<AIExplanationEntity, AIExplanationDTO> {
    private val jsonObjectMapper = OrdJsonMapper.instance

    override fun toDTO(entity: AIExplanationEntity): AIExplanationDTO {
        return AIExplanationDTO(
            id = entity.id!!,
            phrase = entity.phrase,
            language = entity.language,
            explanation = entity.explanation,
            followUps = deserializeFollowUps(entity.followUps),
            kind = entity.kind,
            emoji = entity.emoji,
            suggestedActions = entity.kind?.rankActions(emptyList()) ?: emptyList(),
            createdAt = entity.createdAt
        )
    }

    /** Keys that no longer match a known action (e.g. after one is removed) are skipped. */
    fun deserializeFollowUps(json: Json): Map<ExplainPhraseFollowUpAction, String> {
        val raw: Map<String, String> =
            jsonObjectMapper.readValue(json.asString(), object : TypeReference<Map<String, String>>() {})

        return raw.mapNotNull { (key, text) ->
            ExplainPhraseFollowUpAction.entries.firstOrNull { it.name == key }?.let { it to text }
        }.toMap()
    }
}
