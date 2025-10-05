package com.ord.core.auth.api.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import com.ord.shared.api.annotations.validators.SafeString
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

data class RegisterRequest(
    @field:SafeString(fieldName = "User name")
    val name: String,

    @field:Email(message = "Invalid email address")
    @field:SafeString(fieldName = "Email")
    val email: String,

    @field:SafeString(fieldName = "Password")
    val password: String,

    @field:NotNull(message = "Native language cannot be null")
    @field:ValidLanguageName
    val nativeLanguage: LanguageName
)

