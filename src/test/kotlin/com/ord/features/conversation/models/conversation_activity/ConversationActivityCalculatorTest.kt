package com.ord.features.conversation.models.conversation_activity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("ConversationActivityCalculator")
class ConversationActivityCalculatorTest {

    @Nested
    @DisplayName("buildHeatmap")
    inner class BuildHeatmap {

        @Test
        fun `produces exactly 90 entries for a 90-day range`() {
            val end = LocalDate.of(2026, 4, 3)
            val start = end.minusDays(89)

            val heatmap = ConversationActivityCalculator.buildHeatmap(emptyMap(), start, end)

            assertEquals(90, heatmap.size)
        }

        @Test
        fun `entries are ordered ascending by date`() {
            val end = LocalDate.of(2026, 4, 3)
            val start = end.minusDays(89)

            val heatmap = ConversationActivityCalculator.buildHeatmap(emptyMap(), start, end)

            assertEquals(start.toString(), heatmap.first().date)
            assertEquals(end.toString(), heatmap.last().date)
        }

        @Test
        fun `zero-fills missing days`() {
            val end = LocalDate.of(2026, 4, 3)
            val start = end.minusDays(89)
            val sparseData = mapOf(start to 5L, end to 3L)

            val heatmap = ConversationActivityCalculator.buildHeatmap(sparseData, start, end)

            assertEquals(5L, heatmap.first().count)
            assertEquals(3L, heatmap.last().count)
            assertEquals(0L, heatmap[1].count)
        }

        @Test
        fun `all zeros produce all P0 percentiles`() {
            val end = LocalDate.of(2026, 4, 3)
            val start = end.minusDays(89)

            val heatmap = ConversationActivityCalculator.buildHeatmap(emptyMap(), start, end)

            heatmap.forEach { day ->
                assertEquals(HeatmapPercentile.P0, day.percentile)
            }
        }

        @Test
        fun `percentiles distribute across known data`() {
            val start = LocalDate.of(2026, 1, 1)
            val end = start.plusDays(9) // 10 days for simplicity

            // Create data: days 0-1=0, days 2-3=1, days 4-5=2, days 6-7=3, days 8-9=5
            val data = mapOf(
                start.plusDays(2) to 1L,
                start.plusDays(3) to 1L,
                start.plusDays(4) to 2L,
                start.plusDays(5) to 2L,
                start.plusDays(6) to 3L,
                start.plusDays(7) to 3L,
                start.plusDays(8) to 5L,
                start.plusDays(9) to 5L,
            )

            val heatmap = ConversationActivityCalculator.buildHeatmap(data, start, end)

            // Days with 0 -> P0
            assertEquals(HeatmapPercentile.P0, heatmap[0].percentile)
            assertEquals(HeatmapPercentile.P0, heatmap[1].percentile)

            // Days with 5 (top 20%) -> P80
            assertEquals(HeatmapPercentile.P80, heatmap[8].percentile)
            assertEquals(HeatmapPercentile.P80, heatmap[9].percentile)
        }
    }

    @Nested
    @DisplayName("buildWeeklySeries")
    inner class BuildWeeklySeries {

        @Test
        fun `produces weekly points covering the full range`() {
            val start = LocalDate.of(2026, 1, 5) // Monday
            val end = start.plusDays(89) // 90 days

            val series = ConversationActivityCalculator.buildWeeklySeries(emptyMap(), start, end)

            // 90 days from a Monday = 13 full weeks (91 days covers Mon to Sun)
            // but 90 days = 12 full weeks + 6 days, so 13 weekly points
            assert(series.size in 13..14) { "Expected 13-14 weeks, got ${series.size}" }
        }

        @Test
        fun `weekRange format is day-of-month span`() {
            val start = LocalDate.of(2026, 3, 16) // Monday
            val end = LocalDate.of(2026, 3, 22)   // Sunday

            val series = ConversationActivityCalculator.buildWeeklySeries(emptyMap(), start, end)

            assertEquals(1, series.size)
            assertEquals("16-22", series[0].weekRange)
        }

        @Test
        fun `cross-month weekRange uses wrapping day numbers`() {
            val start = LocalDate.of(2026, 3, 30) // Monday
            val end = LocalDate.of(2026, 4, 5)    // Sunday

            val series = ConversationActivityCalculator.buildWeeklySeries(emptyMap(), start, end)

            assertEquals(1, series.size)
            assertEquals("30-5", series[0].weekRange)
        }

        @Test
        fun `sums daily counts within each week`() {
            val start = LocalDate.of(2026, 3, 16) // Monday
            val end = LocalDate.of(2026, 3, 29)   // Sunday (2 weeks)

            val data = mapOf(
                LocalDate.of(2026, 3, 16) to 3L,
                LocalDate.of(2026, 3, 17) to 2L,
                LocalDate.of(2026, 3, 23) to 7L,
            )

            val series = ConversationActivityCalculator.buildWeeklySeries(data, start, end)

            assertEquals(2, series.size)
            assertEquals(5L, series[0].count) // 3 + 2
            assertEquals(7L, series[1].count)
        }

        @Test
        fun `partial first week when start is not Monday`() {
            val start = LocalDate.of(2026, 3, 19) // Thursday
            val end = LocalDate.of(2026, 3, 29)   // Sunday

            val data = mapOf(
                LocalDate.of(2026, 3, 16) to 10L, // Monday, before start — should not count
                LocalDate.of(2026, 3, 19) to 4L,  // Thursday, start day
                LocalDate.of(2026, 3, 22) to 1L,  // Sunday, end of first week
            )

            val series = ConversationActivityCalculator.buildWeeklySeries(data, start, end)

            // First week starts Mon 16, but effective start is Thu 19
            assertEquals("16-22", series[0].weekRange)
            assertEquals(5L, series[0].count) // 4 + 1 (Mon 16 is outside window)
        }
    }

    @Nested
    @DisplayName("computePercentile")
    inner class ComputePercentile {

        @Test
        fun `zero always returns P0`() {
            val sorted = listOf(0L, 0L, 1L, 2L, 3L)
            assertEquals(HeatmapPercentile.P0, ConversationActivityCalculator.computePercentile(0L, sorted))
        }

        @Test
        fun `highest value in a uniform distribution returns P80`() {
            // 10 values: 1..10
            val sorted = (1L..10L).toList()
            // count=10 at index 9, rank = 9/10 = 0.9 >= 0.8 → P80
            assertEquals(HeatmapPercentile.P80, ConversationActivityCalculator.computePercentile(10L, sorted))
        }

        @Test
        fun `lowest non-zero value returns P0 when rank is below 0_2`() {
            // 10 values: 1,1,1,1,1,1,1,1,1,10
            val sorted = List(9) { 1L } + listOf(10L)
            // count=1, indexOfFirst{it>=1} = 0, rank = 0/10 = 0.0 → P0
            assertEquals(HeatmapPercentile.P0, ConversationActivityCalculator.computePercentile(1L, sorted))
        }
    }
}
