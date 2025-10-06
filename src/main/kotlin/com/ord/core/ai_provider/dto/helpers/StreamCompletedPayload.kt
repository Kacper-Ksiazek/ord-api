package com.ord.core.ai_provider.dto.helpers

data class StreamCompletedPayload<TFinalContent>(
    val finalContent: TFinalContent,
    val inputTokens: Int,
    val outputTokens: Int,
) {
    val totalTokens: Int
        get() = inputTokens + outputTokens
}