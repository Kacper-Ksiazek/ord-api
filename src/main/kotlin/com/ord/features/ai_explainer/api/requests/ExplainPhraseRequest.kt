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
    val language: LanguageName,

    @field:Size(max = 2000, message = "Context must be at most 2000 characters")
    @Schema(
        description = "Optional context where the phrase is used (e.g., a paragraph or sentence containing the phrase)",
        example = "I was reading a book and came across this phrase: 'break the ice'. The context was about meeting new people at a party.",
        nullable = true,
        maxLength = 2000
    )
    val context: String? = null,

    @field:Size(max = 500, message = "Custom instruction must be at most 500 characters")
    @Schema(
        description = "Optional custom instruction in any language to guide the explanation (e.g., 'Focus on informal usage', 'Explain like I'm 5', 'Include etymology')",
        example = "Please focus on how this is used in everyday conversation",
        nullable = true,
        maxLength = 500
    )
    val customInstruction: String? = null
)
