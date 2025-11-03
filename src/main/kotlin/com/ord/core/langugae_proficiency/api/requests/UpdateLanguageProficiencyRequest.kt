package com.ord.core.langugae_proficiency.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import jakarta.validation.constraints.NotNull

data class UpdateLanguageProficiencyRequest(
    @field:NotNull(message = "Language cannot be null")
    val language: LanguageName,

    val level: LanguageProficiencyLevel? = null,

    @field:ValidLanguageName
    val translateTo: LanguageName? = null,

    @field:ValidLanguageName
    val generativeContentLanguage: LanguageName? = null,
)
