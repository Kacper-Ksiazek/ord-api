package com.ord.core.ai_provider.dto

data class OpenAIRequest(
    val model: String,
    val input: String,
    val instructions: String? = null,
    val temperature: Float,
    val stream: Boolean = false,
//    val reasoning: Map<String, String> = mapOf(
//        "effort" to "low"
//    ),
    val max_output_tokens: Int,
)
