package com.ord.features.home.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * UTC bounds shared with conversation activity overview:
 * a half-open range `[startOfDay, startOfNextDay)`.
 * The rolling 30-day window includes today and the previous 29 days.
 * Month bounds are the current UTC calendar month, `[firstDay, startOfTomorrow)`.
 */
data class HomeWindow(
    val today: LocalDate,
    val year: Int,
    val from30Inclusive: Instant,
    val fromMonthInclusive: Instant,
    val from90Inclusive: Instant,
    val toExclusive: Instant,
    val yearStartInclusive: Instant,
    val yearEndExclusive: Instant,
) {
    companion object {
        fun at(today: LocalDate): HomeWindow {
            val from30 = today.minusDays(29)
            val from90 = today.minusDays(89)
            val monthStart = today.withDayOfMonth(1)

            return HomeWindow(
                today = today,
                year = today.year,
                from30Inclusive = from30.atStartOfDay(ZoneOffset.UTC).toInstant(),
                fromMonthInclusive = monthStart.atStartOfDay(ZoneOffset.UTC).toInstant(),
                from90Inclusive = from90.atStartOfDay(ZoneOffset.UTC).toInstant(),
                toExclusive = today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                yearStartInclusive = LocalDate.of(today.year, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                yearEndExclusive = LocalDate.of(today.year + 1, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(),
            )
        }
    }
}
