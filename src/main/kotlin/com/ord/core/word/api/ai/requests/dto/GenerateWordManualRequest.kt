package com.ord.core.word.api.ai.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageProficiencyLevel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class GenerateWordManualRequest(
    @field:NotBlank(message = "Word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Word must be between 1 and 255 characters")
    val word: String,

    @field:NotNull(message = "Language cannot be null")
    val language: LanguageName,

    @field:ValidLanguageName
    val targetLanguage: LanguageName? = null,

    @field:ValidLanguageProficiencyLevel
    val proficiencyLevel: LanguageProficiencyLevel? = null
)