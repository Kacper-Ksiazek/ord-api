package com.ord.features.ai_explainer.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

@Schema(description = "Request to suggest refine actions for a finished explanation")
data class ExplainPhraseSuggestActionsRequest(
    @field:NotBlank(message = "Word/phrase cannot be blank")
    @field:Size(min = 1, max = 255, message = "Word/phrase must be between 1 and 255 characters")
    @Schema(description = "What the learner typed", example = "hund", required = true, maxLength = 255)
    val phrase: String,

    @field:NotNull(message = "Language cannot be null")
    @Schema(description = "Language of the word/phrase", example = "NORWEGIAN", required = true)
    val language: LanguageName,

    @field:NotBlank(message = "Explanation cannot be blank")
    @field:Size(min = 1, max = 4000, message = "Explanation must be between 1 and 4000 characters")
    @Schema(description = "The explanation text shown to the learner", required = true, maxLength = 4000)
    val explanation: String,

    @Schema(
        description = "Id the explanation is stored under; when set, the detected kind is saved on it",
        nullable = true,
    )
    val explanationId: UUID? = null,
)
