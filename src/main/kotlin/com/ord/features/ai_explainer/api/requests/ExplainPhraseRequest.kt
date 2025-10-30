package com.ord.features.ai_explainer.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "Request to get an AI-powered explanation of a word or phrase")
data class ExplainPhraseRequest(
    @field:NotBlank(message = "Word/phrase cannot be blank")
    @field:Size(min = 1, max = 255, message = "Word/phrase must be between 1 and 255 characters")
    @Schema(
        description = "The word or phrase to explain",
        example = "hund",
        required = true,
        maxLength = 255
    )
    val phrase: String,

    @field:NotNull(message = "Language cannot be null")
    @Schema(
        description = "Language of the word/phrase",
        example = "NORWEGIAN",
        required = true
    )
    val language: LanguageName
)
