package com.ord.features.conversation.models.conversation_activity

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object ConversationActivityCalculator {

    fun buildHeatmap(
        dailyCounts: Map<LocalDate, Long>,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<HeatmapDayDTO> {
        val days = generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endDate) }
            .toList()

        val counts = days.map { dailyCounts.getOrDefault(it, 0L) }
        val sortedCounts = counts.sorted()

        return days.zip(counts).map { (date, count) ->
            HeatmapDayDTO(
                count = count,
                date = date.toString(),
                percentile = computePercentile(count, sortedCounts),
            )
        }
    }

    fun buildWeeklySeries(
        dailyCounts: Map<LocalDate, Long>,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<ActivityWeekPointDTO> {
        var weekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val result = mutableListOf<ActivityWeekPointDTO>()

        while (!weekStart.isAfter(endDate)) {
            val weekEnd = weekStart.plusDays(6)
            val effectiveStart = maxOf(weekStart, startDate)
            val effectiveEnd = minOf(weekEnd, endDate)

            val count = generateSequence(effectiveStart) { it.plusDays(1) }
                .takeWhile { !it.isAfter(effectiveEnd) }
                .sumOf { dailyCounts.getOrDefault(it, 0L) }

            val weekRange = "${weekStart.dayOfMonth}-${weekEnd.dayOfMonth}"
            result.add(ActivityWeekPointDTO(weekRange = weekRange, count = count))

            weekStart = weekStart.plusWeeks(1)
        }

        return result
    }

    fun computePercentile(count: Long, sortedCounts: List<Long>): HeatmapPercentile {
        if (count == 0L) return HeatmapPercentile.P0

        val rank = sortedCounts.indexOfFirst { it >= count }.toDouble() / sortedCounts.size

        return when {
            rank >= 0.8 -> HeatmapPercentile.P80
            rank >= 0.6 -> HeatmapPercentile.P60
            rank >= 0.4 -> HeatmapPercentile.P40
            rank >= 0.2 -> HeatmapPercentile.P20
            else -> HeatmapPercentile.P0
        }
    }
}
