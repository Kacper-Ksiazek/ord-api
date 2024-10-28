package com.backend.ord.seeders.mocks.words_gpt_tokens_usage.json_data_models

import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.TokensUsage.WordsGPTTokensConsumptionType
import java.math.BigDecimal
import java.util.UUID

data class DetailedWordsTokensUsage(
    val id: UUID,
    val word: String,
    val cost: BigDecimal,
    val inputTokens: Int,
    val outputTokens: Int,
    val priceForMlnInputTokens: BigDecimal,
    val priceForMlnOutputTokens: BigDecimal,

    val translatedTo: LanguageName,
    val translatedFrom: LanguageName,
    val consumptionType: WordsGPTTokensConsumptionType,

    /** This property is present but should not be used */
    val createdAt: String,
)
