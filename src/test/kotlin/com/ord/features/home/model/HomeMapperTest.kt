package com.ord.features.home.model

import com.ord.core.word.models.word.enums.WordType
import com.ord.features.home.model.parts.HomeActivityDay
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

@DisplayName("HomeMapper")
class HomeMapperTest {

    @Nested
    @DisplayName("window")
    inner class Window {

        @Test
        fun `30-day window includes today and the previous 29 UTC days`() {
            val today = LocalDate.of(2026, 9, 30)

            val window = HomeWindow.at(today)

            assertEquals(LocalDate.of(2026, 9, 1).atStartOfDayUtc(), window.from30Inclusive)
            assertEquals(LocalDate.of(2026, 9, 1).atStartOfDayUtc(), window.fromMonthInclusive)
            assertEquals(LocalDate.of(2026, 10, 1).atStartOfDayUtc(), window.toExclusive)
            assertEquals(2026, window.year)
            assertEquals(LocalDate.of(2026, 1, 1).atStartOfDayUtc(), window.yearStartInclusive)
            assertEquals(LocalDate.of(2027, 1, 1).atStartOfDayUtc(), window.yearEndExclusive)
        }

        @Test
        fun `year boundary stays on the UTC calendar year of today`() {
            val today = LocalDate.of(2026, 1, 2)

            val window = HomeWindow.at(today)

            assertEquals(LocalDate.of(2025, 12, 4).atStartOfDayUtc(), window.from30Inclusive)
            assertEquals(LocalDate.of(2026, 1, 1).atStartOfDayUtc(), window.fromMonthInclusive)
            assertEquals(LocalDate.of(2026, 1, 3).atStartOfDayUtc(), window.toExclusive)
            assertEquals(LocalDate.of(2026, 1, 1).atStartOfDayUtc(), window.yearStartInclusive)
        }
    }

    @Nested
    @DisplayName("response")
    inner class Response {

        private val window = HomeWindow.at(LocalDate.of(2026, 9, 30))

        @Test
        fun `empty language summary is zeros`() {
            val response = window.emptyHomeResponse()

            assertEquals(0L, response.overviews.words.total)
            assertEquals(0L, response.overviews.words.addedLast30Days)
            assertEquals(emptyMap<WordType, Long>(), response.overviews.words.byType)
            assertEquals(0L, response.overviews.conversations.total)
            assertEquals(0L, response.overviews.conversations.messagesTotal)
            assertEquals(0L, response.overviews.conversations.createdLast30Days)
            assertEquals(0L, response.overviews.conversations.messagesLast30Days)
            assertEquals(true, response.overviews.games.comingSoon)
            assertEquals(0L, response.overviews.games.total)
            assertEquals(0L, response.overviews.games.last30Days)
            assertEquals(emptyList<Any>(), response.recentContent.words)
            assertEquals(emptyList<Any>(), response.recentContent.conversations)
            assertEquals(HOME_TREND_DAY_COUNT, response.overviews.words.trend.size)
            assertEquals(0L, response.overviews.words.trend.sumOf { it.count })
        }

        @Test
        fun `dense trend days fill missing dates with zero`() {
            val window = HomeWindow.at(LocalDate.of(2026, 3, 31))
            val dense = window.denseTrend(
                listOf(HomeActivityDay(date = "2026-03-30", count = 2)),
            )

            assertEquals(HOME_TREND_DAY_COUNT, dense.size)
            assertEquals("2026-01-01", dense.first().date)
            assertEquals("2026-03-31", dense.last().date)
            assertEquals(2L, dense.find { it.date == "2026-03-30" }?.count)
        }

        @Test
        fun `omits word types with zero and keeps enum order`() {
            val counts = mapOf(
                WordType.VERB to 2L,
                WordType.NOUN to 0L,
                WordType.PHRASE to 1L,
            ).omitZeros()

            assertEquals(listOf(WordType.VERB, WordType.PHRASE), counts.keys.toList())
            assertEquals(2L, counts[WordType.VERB])
            assertEquals(1L, counts[WordType.PHRASE])
        }

        @Test
        fun `drops zero activity days, sums duplicates, and sorts by date`() {
            val days = listOf(
                HomeActivityDay(date = "2026-09-30", count = 1),
                HomeActivityDay(date = "2026-09-02", count = 0),
                HomeActivityDay(date = "2026-09-30", count = 3),
                HomeActivityDay(date = "2026-01-04", count = 2),
            ).mergeByDate()

            assertEquals(
                listOf(
                    HomeActivityDay(date = "2026-01-04", count = 2),
                    HomeActivityDay(date = "2026-09-30", count = 4),
                ),
                days,
            )
        }

        @Test
        fun `keeps a null games count when finished games are not available`() {
            val response = HomeSnapshot(
                wordsTotal = 1,
                wordsAddedLast30Days = 1,
                wordsByType = mapOf(WordType.NOUN to 1),
                conversationsTotal = 0,
                conversationsCreatedLast30Days = 0,
                messagesTotal = 0,
                messagesLast30Days = 0,
                gamesTotal = null,
                gamesLast30Days = null,
                wordsAddedTrend90 = emptyList(),
                conversationsCreatedTrend90 = emptyList(),
                messagesTrend90 = emptyList(),
                gamesFinishedTrend90 = emptyList(),
            ).toHomeResponse(window)

            assertEquals(true, response.overviews.games.comingSoon)
            assertEquals(null, response.overviews.games.total)
            assertEquals(null, response.overviews.games.last30Days)
        }
    }

    private fun LocalDate.atStartOfDayUtc(): Instant =
        atStartOfDay(java.time.ZoneOffset.UTC).toInstant()
}
