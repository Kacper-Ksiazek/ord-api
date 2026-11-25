package com.ord.features.ai_explainer.api.responses

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "The learner's request rewritten to be clearer")
data class ExplainPhraseEnhanceResponse(
    @Schema(description = "Improved request, at most 255 characters", maxLength = 255)
    val prompt: String,
)
