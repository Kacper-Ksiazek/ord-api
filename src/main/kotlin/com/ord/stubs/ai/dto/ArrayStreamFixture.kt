package com.ord.stubs.ai.dto

import tools.jackson.databind.JsonNode

data class ArrayStreamFixture(
    val items: List<JsonNode>,
    val inputTokens: Int = 55,
    val outputTokens: Int = 22,
)
