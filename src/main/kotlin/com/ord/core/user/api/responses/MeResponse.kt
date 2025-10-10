package com.ord.core.user.api.responses

import com.ord.core.langugae_proficiency.model.enums.LanguageName

data class MeResponse(
    val name: String,
    val email: String,
    val nativeLanguage: LanguageName,
    val selectedLearningLanguage: LanguageName?,
)
