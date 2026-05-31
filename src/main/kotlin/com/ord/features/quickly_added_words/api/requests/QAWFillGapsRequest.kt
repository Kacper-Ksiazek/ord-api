package com.ord.features.quickly_added_words.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size

@Schema(description = "Request to AI-fill missing QAW fields for a batch of words")
data class QAWFillGapsRequest(
    @Schema(
        description = "Language of the words being enriched",
        example = "ENGLISH",
        required = true,
    )
    val language: LanguageName,

    @field:Valid
    @field:Size(min = 1, max = 20, message = "Items list must contain between 1 and 20 words")
    @Schema(
        description = "Words to enrich (only the word field is required per item)",
        required = true,
        minLength = 1,
        maxLength = 20,
    )
    val items: List<QAWFillGapsItem>,
)
