package com.ord.core.auth.api.requests.dto

import com.ord.shared.api.annotations.validators.SafeString
import jakarta.validation.constraints.Email

class LoginRequest(
    @field:Email(message = "Invalid email address")
    @field:SafeString(fieldName = "Email")
    val email: String,

    @field:SafeString(fieldName = "Password")
    val password: String
)
