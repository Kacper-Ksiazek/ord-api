package com.ord.core.auth.api.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName

// TODO: Add validation annotations
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val nativeLanguage: LanguageName
)

