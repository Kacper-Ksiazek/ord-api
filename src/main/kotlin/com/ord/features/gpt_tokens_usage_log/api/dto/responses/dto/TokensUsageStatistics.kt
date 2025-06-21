package com.ord.features.gpt_tokens_usage_log.api.dto.responses.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import java.math.BigDecimal

data class TokensUsageStatistics<T>(
    val consumptionType: T,
    val translatedFrom: LanguageName,

    val amount: Long,
    val totalCost: BigDecimal,

    val avgInputTokens: Double,
    val avgOutputTokens: Double
)