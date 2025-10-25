package com.ord.core.auth.api.requests.dto

import com.ord.shared.api.annotations.validators.SafeString
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

@Schema(description = "Request to verify OTP code and authenticate user")
data class OtpVerifyDto(
    @field:Email(message = "Invalid email address")
    @field:SafeString(fieldName = "Email")
    @Schema(
        description = "Email address associated with the OTP code",
        example = "user@example.com",
        required = true
    )
    val email: String,

    @field:Pattern(regexp = "^[0-9]{6}$", message = "OTP code must be exactly 6 digits")
    @field:SafeString(fieldName = "Code")
    @Schema(
        description = "6-digit OTP code sent to the email address",
        example = "123456",
        pattern = "^[0-9]{6}$",
        required = true
    )
    val code: String
)
