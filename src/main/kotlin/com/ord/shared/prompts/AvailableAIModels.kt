package com.ord.shared.prompts

import java.math.BigDecimal

enum class AvailableAIModels(
    val model: String,
    val pricePerMlnInputTokens: BigDecimal,
    val pricePerMlnOutputTokens: BigDecimal,
) {
    GPT_6_LUNA(
        model = "gpt-6-luna",
        pricePerMlnInputTokens = BigDecimal("0.20"),
        pricePerMlnOutputTokens = BigDecimal("1.20"),
    );

    companion object {
        val DEFAULT = GPT_6_LUNA
    }
}
