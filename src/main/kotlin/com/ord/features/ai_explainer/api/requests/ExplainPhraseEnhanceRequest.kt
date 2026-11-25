package com.ord.features.ai_explainer.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "What the learner typed into the explain input, to be rewritten into a clearer request")
data class ExplainPhraseEnhanceRequest(
    @field:NotBlank(message = "Prompt cannot be blank")
    @field:Size(min = 1, max = 255, message = "Prompt must be between 1 and 255 characters")
    @Schema(
        description = "What the learner has typed so far",
        example = "tell me more about a word contraction",
        required = true,
        maxLength = 255
    )
    val prompt: String,

    @field:NotNull(message = "Language cannot be null")
    @Schema(
        description = "Language the learner is asking about",
        example = "ENGLISH",
        required = true
    )
    val language: LanguageName,
)
