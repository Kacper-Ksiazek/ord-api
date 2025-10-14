package com.ord.core.user.api.requests

import com.ord.core.langugae_proficiency.model.LanguageProficiencyCompactDTO
import com.ord.core.langugae_proficiency.model.enums.LanguageName

data class InitUserAccountRequest(
    val name: String,
    val nativeLanguage: LanguageName,
    val selectedLearningLanguage: LanguageName,
    val languageProficiencies: List<LanguageProficiencyCompactDTO>
)
