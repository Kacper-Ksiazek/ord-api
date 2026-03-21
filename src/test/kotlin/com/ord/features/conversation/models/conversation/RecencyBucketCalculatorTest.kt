package com.ord.features.conversation.models.conversation

import com.ord.features.conversation.models.conversation.enums.RecencyBucket
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneOffset

@DisplayName("computeRecencyBucket")
class RecencyBucketCalculatorTest {

    private fun instant(date: LocalDate) = date.atStartOfDay().toInstant(ZoneOffset.UTC)

    @Nested
    @DisplayName("TODAY")
    inner class Today {
        @Test
        fun `activity on same day as now`() {
            val now = LocalDate.of(2026, 3, 21)
            assertEquals(RecencyBucket.TODAY, computeRecencyBucket(instant(now), instant(now)))
        }

        @Test
        fun `future activity on same calendar day is still TODAY`() {
            val today = LocalDate.of(2026, 3, 21)
            val futureInstant = today.atTime(23, 59).toInstant(ZoneOffset.UTC)
            assertEquals(RecencyBucket.TODAY, computeRecencyBucket(futureInstant, instant(today)))
        }
    }

    @Nested
    @DisplayName("YESTERDAY")
    inner class Yesterday {
        @Test
        fun `activity exactly one day before now`() {
            val now = LocalDate.of(2026, 3, 21)
            val yesterday = now.minusDays(1)
            assertEquals(RecencyBucket.YESTERDAY, computeRecencyBucket(instant(yesterday), instant(now)))
        }

        @Test
        fun `Sunday is YESTERDAY when now is Monday`() {
            val monday = LocalDate.of(2026, 3, 23)
            val sunday = monday.minusDays(1)
            assertEquals(RecencyBucket.YESTERDAY, computeRecencyBucket(instant(sunday), instant(monday)))
        }
    }

    @Nested
    @DisplayName("THIS_WEEK")
    inner class ThisWeek {
        @Test
        fun `activity on Monday of current week (now is Wednesday)`() {
            val now = LocalDate.of(2026, 3, 25) // Wednesday
            val monday = LocalDate.of(2026, 3, 23)
            assertEquals(RecencyBucket.THIS_WEEK, computeRecencyBucket(instant(monday), instant(now)))
        }

        @Test
        fun `activity on Tuesday of current week (now is Friday)`() {
            val now = LocalDate.of(2026, 3, 27) // Friday
            val tuesday = LocalDate.of(2026, 3, 24)
            assertEquals(RecencyBucket.THIS_WEEK, computeRecencyBucket(instant(tuesday), instant(now)))
        }

        @Test
        fun `no THIS_WEEK dates when now is Monday (week just started)`() {
            val monday = LocalDate.of(2026, 3, 23)
            // yesterday is Sunday = before weekMonday, so only TODAY and YESTERDAY are possible
            val saturday = LocalDate.of(2026, 3, 21)
            val bucket = computeRecencyBucket(instant(saturday), instant(monday))
            // Saturday is before this week's Monday → not THIS_WEEK
            assert(bucket != RecencyBucket.THIS_WEEK) { "Saturday should not be THIS_WEEK when today is Monday" }
        }
    }

    @Nested
    @DisplayName("THIS_MONTH")
    inner class ThisMonth {
        @Test
        fun `activity at start of same month, before this week`() {
            val now = LocalDate.of(2026, 3, 21) // Saturday (weekMonday = 2026-03-16)
            val earlyInMonth = LocalDate.of(2026, 3, 1)
            assertEquals(RecencyBucket.THIS_MONTH, computeRecencyBucket(instant(earlyInMonth), instant(now)))
        }

        @Test
        fun `activity on day before week start in same month`() {
            val now = LocalDate.of(2026, 3, 21)
            val dayBeforeWeek = LocalDate.of(2026, 3, 15) // Sunday before week starting Mar 16
            assertEquals(RecencyBucket.THIS_MONTH, computeRecencyBucket(instant(dayBeforeWeek), instant(now)))
        }

        @Test
        fun `activity in previous month is LATER not THIS_MONTH`() {
            val now = LocalDate.of(2026, 3, 21)
            val lastMonth = LocalDate.of(2026, 2, 28)
            assertEquals(RecencyBucket.LATER, computeRecencyBucket(instant(lastMonth), instant(now)))
        }
    }

    @Nested
    @DisplayName("LATER")
    inner class Later {
        @Test
        fun `activity in previous year`() {
            val now = LocalDate.of(2026, 3, 21)
            val lastYear = LocalDate.of(2025, 12, 31)
            assertEquals(RecencyBucket.LATER, computeRecencyBucket(instant(lastYear), instant(now)))
        }

        @Test
        fun `future activity on a different day`() {
            val now = LocalDate.of(2026, 3, 21)
            val futureDay = LocalDate.of(2026, 3, 22)
            assertEquals(RecencyBucket.LATER, computeRecencyBucket(instant(futureDay), instant(now)))
        }
    }
}
