package com.ord.features.ai_explainer.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.validators.annotations.FollowUpContextRequired
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@FollowUpContextRequired
@Schema(description = "Request to follow up on a previous AI explanation of a word or phrase")
data class ExplainPhraseFollowUpRequest(
    @field:NotBlank(message = "Word/phrase cannot be blank")
    @field:Size(min = 1, max = 255, message = "Word/phrase must be between 1 and 255 characters")
    @Schema(
        description = "The word or phrase being explained",
        example = "hund",
        required = true,
        maxLength = 255,
    )
    val phrase: String,

    @field:NotNull(message = "Language cannot be null")
    @Schema(
        description = "Language of the word/phrase",
        example = "NORWEGIAN",
        required = true,
    )
    val language: LanguageName,

    @field:NotBlank(message = "Previous explanation cannot be blank")
    @field:Size(min = 1, max = 4000, message = "Previous explanation must be between 1 and 4000 characters")
    @Schema(
        description = "The explanation text already shown to the learner",
        required = true,
        maxLength = 4000,
    )
    val previousExplanation: String,

    @field:NotNull(message = "Action cannot be null")
    @Schema(
        description = "Follow-up action to apply",
        required = true,
    )
    val action: ExplainPhraseFollowUpAction,

    @field:Size(max = 2000, message = "Context must be at most 2000 characters")
    @Schema(
        description = "Optional context where the phrase is used; required when action is IN_THIS_CONTEXT",
        nullable = true,
        maxLength = 2000,
    )
    val context: String? = null,
)
