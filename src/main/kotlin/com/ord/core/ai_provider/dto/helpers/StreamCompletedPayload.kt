package com.ord.core.ai_provider.dto.helpers

data class StreamCompletedPayload(
    val finalContent: String,
    val inputTokens: Int,
    val outputTokens: Int,
) {
    val totalTokens: Int
        get() = inputTokens + outputTokens
}