package com.ord.shared.domain.dto

import com.ord.shared.domain.projections.CountingSummaryProjection

/**
 * Represents a summary of counts for today, this week, and this month.
 * Uses for instance for words in the WordRepository
 */
data class CountingSummary(
    val today: Int,
    val week: Int,
    val month: Int
) {
    constructor(projection: CountingSummaryProjection) : this(
        today = projection.today ?: 0,
        week = projection.week ?: 0,
        month = projection.month ?: 0
    )
}