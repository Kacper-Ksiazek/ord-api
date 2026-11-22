package com.ord.features.ai_explainer.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "Partial text typed into the explain input, to be completed by AI (tab completion)")
data class ExplainPhraseAutocompleteRequest(
    @field:NotBlank(message = "Prefix cannot be blank")
    @field:Size(min = 2, max = 255, message = "Prefix must be between 2 and 255 characters")
    @Schema(
        description = "What the learner has typed so far",
        example = "break the",
        required = true,
        maxLength = 255
    )
    val prefix: String,

    @field:NotNull(message = "Language cannot be null")
    @Schema(
        description = "Language the learner is asking about",
        example = "ENGLISH",
        required = true
    )
    val language: LanguageName,
)
