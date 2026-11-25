package com.ord.features.ai_explainer.api.responses

import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "What the learner asked about and which refine actions fit it")
data class ExplainPhraseSuggestActionsResponse(
    val kind: ExplainPhraseKind,

    @Schema(description = "Every action that fits the kind, most useful first; the UI shows the first few not yet used")
    val actions: List<ExplainPhraseFollowUpAction>,

    @Schema(nullable = true, description = "One emoji representing the topic; null when the model returned none")
    val emoji: String? = null,
)
