package com.ord.testing_utils.mocks.ai.dto

import com.fasterxml.jackson.databind.JsonNode

data class ArrayStreamFixture(
    val items: List<JsonNode>,
    val inputTokens: Int = 55,
    val outputTokens: Int = 22,
)
