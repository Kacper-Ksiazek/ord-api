package com.backend.ord.domain.infrastructure

data class CountingSummary(
    val today: Int,
    val week: Int,
    val month: Int
) {
    constructor(projection: CountingSummaryProjection) : this(
        today = projection.today?.toInt() ?: 0,
        week = projection.week?.toInt() ?: 0,
        month = projection.month?.toInt() ?: 0
    )
}
