package com.ord.core.auth.api.requests.dto

import com.ord.shared.api.annotations.validators.SafeString
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class OtpVerifyDto(
    @field:Email(message = "Invalid email address")
    @field:SafeString(fieldName = "Email")
    val email: String,

    @field:Pattern(regexp = "^[0-9]{6}$", message = "OTP code must be exactly 6 digits")
    @field:SafeString(fieldName = "Code")
    val code: String
)
