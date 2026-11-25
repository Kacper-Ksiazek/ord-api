package com.ord.features.ai_explainer.models.explanation

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

@Schema(description = "An explanation saved after an explain stream finished, with the follow-ups applied to it")
data class AIExplanationDTO(
    val id: UUID,
    val phrase: String,
    val language: LanguageName,
    val explanation: String,

    @Schema(description = "Text of each follow-up already applied, keyed by action")
    val followUps: Map<ExplainPhraseFollowUpAction, String> = emptyMap(),
    @Schema(nullable = true, description = "Null for entries saved before the kind was recorded")
    val kind: ExplainPhraseKind? = null,
    @Schema(nullable = true, description = "One emoji representing the topic; null for entries saved before it was recorded")
    val emoji: String? = null,
    @Schema(description = "Refine actions that fit the kind in default order; empty when the kind is unknown")
    val suggestedActions: List<ExplainPhraseFollowUpAction> = emptyList(),

    val createdAt: Instant
)
