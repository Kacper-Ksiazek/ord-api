package com.ord.core.langugae_proficiency.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel

data class LanguageProficiencyCompactDTO(
    val language: LanguageName,
    var level: LanguageProficiencyLevel,
    var generativeContentLanguage: LanguageName,
)
