package com.ord.core.gpt_tokens_usage_analytics.responses.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Aggregated GPT tokens usage analytics data")
data class GptTokensUsageAnalyticsResponse(
    @Schema(description = "Time-series of daily tokens usage")
    val dailyUsage: List<DailyTokensUsageDTO>,

    @Schema(description = "Tokens usage breakdown by operation type")
    val usageByOperationType: List<TokensUsageByOperationTypeDTO>
)
