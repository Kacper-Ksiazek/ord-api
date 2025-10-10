package com.ord.core.langugae_proficiency.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel

data class UpdateLanguageProficiencyRequest(
    val language: LanguageName,
    val level: LanguageProficiencyLevel? = null,
    val generativeContentLanguage: LanguageName? = null,
)
