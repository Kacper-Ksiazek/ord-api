package com.ord.core.langugae_proficiency.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel

data class CreateLanguageProficiencyRequest(
    val language: LanguageName,
    var level: LanguageProficiencyLevel,
    var generativeContentLanguage: LanguageName? = null,
)