package com.ord.core.langugae_proficiency.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import jakarta.validation.constraints.NotNull

data class CreateLanguageProficiencyRequest(
    @field:NotNull(message = "Language cannot be null")
    val language: LanguageName,

    @field:NotNull(message = "Level cannot be null")
    var level: LanguageProficiencyLevel,

    @field:NotNull(message = "Translate to language cannot be null")
    @field:ValidLanguageName
    var translateTo: LanguageName,

    @field:ValidLanguageName
    var generativeContentLanguage: LanguageName? = null,
)