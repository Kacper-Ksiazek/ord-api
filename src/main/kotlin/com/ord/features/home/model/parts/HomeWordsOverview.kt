package com.ord.features.home.model.parts

import com.ord.core.word.models.word.enums.WordType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Word card: totals and the 90-day sparkline")
data class HomeWordsOverview(
    @Schema(description = "All words in the selected language", example = "40")
    val total: Long,

    @Schema(description = "Words created in the current UTC calendar month, including today", example = "6")
    val addedLast30Days: Long,

    @Schema(description = "Count per word type. Types with zero words are omitted.")
    val byType: Map<WordType, Long>,

    @Schema(description = "Words created per UTC day for the last 90 days, including zeros")
    val trend: List<HomeActivityDay>,
)
