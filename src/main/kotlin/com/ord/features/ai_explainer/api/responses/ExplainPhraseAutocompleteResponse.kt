package com.ord.features.ai_explainer.api.responses

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Suggested continuation of the explain input")
data class ExplainPhraseAutocompleteResponse(
    @Schema(
        description = "Text to append after the prefix exactly as returned (may start with a space). Empty when there is no suggestion.",
        example = " ice",
        required = true
    )
    val completion: String,
)
