package com.ord.features.quickly_added_words.api.requests

import com.ord.shared.api.annotations.validators.SafeString
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "A single word to enrich with AI-generated QAW metadata")
data class QAWFillGapsItem(
    @field:SafeString(fieldName = "Word", min = 1, max = 255)
    @Schema(
        description = "The word or phrase to enrich (only required field per item)",
        example = "verbose",
        required = true,
        minLength = 1,
        maxLength = 255,
    )
    val word: String,
)
