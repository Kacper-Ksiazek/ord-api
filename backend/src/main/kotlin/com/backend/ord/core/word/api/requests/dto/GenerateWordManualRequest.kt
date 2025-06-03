package com.backend.ord.core.word.api.requests.dto

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GenerateWordManualRequest(
    @field:NotBlank(message = "Word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Word must be between 1 and 255 characters")
    val word: String,

    @field:NotBlank(message = "Original language cannot be blank")
    @field:Size(min = 1, max = 50, message = "Original language must be between 1 and 50 characters")
    val language: LanguageName,

    @field:Size(min = 1, max = 255, message = "Target language must be between 1 and 255 characters")
    val targetLanguage: LanguageName? = null,

    @field:Size(min = 1, max = 50, message = "Proficiency level must be between 1 and 50 characters")
    val proficiencyLevel: LanguageProficiencyLevel? = null
)
