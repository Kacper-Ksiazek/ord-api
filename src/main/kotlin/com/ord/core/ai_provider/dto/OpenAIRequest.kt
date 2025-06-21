package com.ord.core.ai_provider.dto

data class OpenAIRequest(
    val model: String,
    val input: String,
    val instructions: String? = null,
    val temperature: Float,

    val max_output_tokens: Int,
)
