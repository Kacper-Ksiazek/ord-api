package com.ord.core.auth.api.requests.dto

import com.ord.core.auth.model.enums.UiLocale
import com.ord.shared.api.annotations.validators.SafeString
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

@Schema(description = "Request to send OTP code to email address")
data class OtpRequestDto(
    @field:Email(message = "Invalid email address")
    @field:SafeString(fieldName = "Email")
    @Schema(
        description = "Email address to send OTP code to",
        example = "user@example.com",
        required = true
    )
    val email: String,
    @Schema(
        description = "UI locale for the OTP email (ord-frontend: en, pl, de). Defaults to en when omitted.",
        example = "en",
        required = false,
    )
    val locale: UiLocale? = null,
)
