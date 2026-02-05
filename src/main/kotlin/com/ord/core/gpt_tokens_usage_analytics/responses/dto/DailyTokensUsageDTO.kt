package com.ord.core.gpt_tokens_usage_analytics.responses.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Aggregated tokens usage for a single day")
data class DailyTokensUsageDTO(
    @Schema(description = "Date of the usage", example = "2026-02-01")
    val date: LocalDate,

    @Schema(description = "Total input tokens consumed on this day", example = "1200")
    val inputTokens: Long,

    @Schema(description = "Total output tokens generated on this day", example = "800")
    val outputTokens: Long
)
