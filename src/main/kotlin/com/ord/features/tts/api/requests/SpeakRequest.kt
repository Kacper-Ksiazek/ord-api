package com.ord.features.tts.api.requests

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Request to synthesize speech from text")
data class SpeakRequest(
    @field:NotBlank(message = "Text cannot be blank")
    @field:Size(min = 1, max = 5000, message = "Text must be between 1 and 5000 characters")
    @Schema(
        description = "The assistant reply text to synthesize",
        example = "That's a great question! Let me explain...",
        required = true,
        minLength = 1,
        maxLength = 5000,
    )
    val text: String,
)
