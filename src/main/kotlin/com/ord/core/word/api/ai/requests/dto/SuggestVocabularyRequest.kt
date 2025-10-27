package com.ord.core.word.api.ai.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageProficiencyLevel
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "Request to generate AI-powered vocabulary suggestions")
data class SuggestVocabularyRequest(
    @field:NotNull(message = "Language cannot be null")
    @Schema(
        description = "Target language for vocabulary suggestions",
        example = "SPANISH",
        required = true
    )
    val language: LanguageName,

    @field:Size(max = 500, message = "Context must be at most 500 characters")
    @Schema(
        description = "Optional context or scenario for vocabulary suggestions (e.g., 'for daily conversation in a grocery store')",
        example = "for business meetings and professional emails",
        nullable = true,
        maxLength = 500
    )
    val context: String? = null,

    @field:Size(max = 1000, message = "Cannot exclude more than 1000 words")
    @Schema(
        description = "Optional list of words to exclude from suggestions (e.g., already suggested words in previous requests)",
        example = "[\"hola\", \"adiós\", \"gracias\"]",
        nullable = true,
        maxLength = 1000
    )
    val excludedWords: List<String>? = null,
)
