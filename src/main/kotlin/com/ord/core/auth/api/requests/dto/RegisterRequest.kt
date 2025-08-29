package com.ord.core.auth.api.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.shared.api.annotations.validators.SafeString
import jakarta.validation.Valid
import jakarta.validation.constraints.Email

data class RegisterRequest(
    @field:SafeString(fieldName = "User name")
    val name: String,

    @field:Email(message = "Invalid email address")
    @field:SafeString(fieldName = "Email")
    val email: String,

    @field:SafeString(fieldName = "Password")
    val password: String,

    @field:Valid
    val nativeLanguage: LanguageName
)

