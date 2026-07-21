package com.ord.stubs.ai.dto

data class StringStreamFixture(
    val chunks: List<String>,
    val inputTokens: Int = 42,
    val outputTokens: Int = 18,
)
