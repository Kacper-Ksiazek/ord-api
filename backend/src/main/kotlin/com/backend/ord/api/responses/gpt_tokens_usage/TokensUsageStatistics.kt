package com.backend.ord.api.responses.gpt_tokens_usage

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import java.math.BigDecimal

data class TokensUsageStatistics<T>(
    val consumptionType: T,
    val translatedFrom: LanguageName,

    val amount: Long,
    val totalCost: BigDecimal,

    val avgInputTokens: Double,
    val avgOutputTokens: Double
)