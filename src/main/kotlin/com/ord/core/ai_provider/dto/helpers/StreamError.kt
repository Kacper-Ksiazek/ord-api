package com.ord.core.ai_provider.dto.helpers

import org.springframework.http.HttpStatus

// TODO: Use it
data class StreamError(
    val type: String = "error",
    val status: HttpStatus,
    val message: String,
)