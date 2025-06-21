package com.ord.core.word.api.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class GenerateWordManualRequest(
    @field:NotBlank(message = "Word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Word must be between 1 and 255 characters")
    val word: String,

    @field:NotNull(message = "Language cannot be null")
    val language: LanguageName,

    val targetLanguage: LanguageName? = null,
    val proficiencyLevel: LanguageProficiencyLevel? = null
)
