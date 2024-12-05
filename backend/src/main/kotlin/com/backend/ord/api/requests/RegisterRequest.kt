package com.backend.ord.api.requests

import com.backend.ord.enums.Language.LanguageName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val nativeLanguage: LanguageName
)

