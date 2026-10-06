package com.ord.features.home.model

import com.ord.core.word.models.word.enums.WordType
import com.ord.features.home.model.parts.HomeActivityDay
import com.ord.features.home.model.parts.HomeConversationsOverview
import com.ord.features.home.model.parts.HomeGamesOverview
import com.ord.features.home.model.parts.HomeOverviews
import com.ord.features.home.model.parts.HomeRecentContent
import com.ord.features.home.model.parts.HomeWordsOverview
import java.time.format.DateTimeFormatter

internal const val HOME_TREND_DAY_COUNT: Int = 90

private val trendDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun HomeWindow.emptyHomeResponse(): HomeResponse = HomeSnapshot(
    wordsTotal = 0,
    wordsAddedLast30Days = 0,
    wordsByType = emptyMap(),
    conversationsTotal = 0,
    conversationsCreatedLast30Days = 0,
    messagesTotal = 0,
    messagesLast30Days = 0,
    gamesTotal = 0,
    gamesLast30Days = 0,
    wordsAddedTrend90 = emptyList(),
    conversationsCreatedTrend90 = emptyList(),
    messagesTrend90 = emptyList(),
    gamesFinishedTrend90 = emptyList(),
).toHomeResponse(this)

fun HomeSnapshot.toHomeResponse(window: HomeWindow): HomeResponse = HomeResponse(
    overviews = HomeOverviews(
        words = HomeWordsOverview(
            total = wordsTotal,
            addedLast30Days = wordsAddedLast30Days,
            byType = wordsByType.omitZeros(),
            trend = window.denseTrend(wordsAddedTrend90),
        ),
        conversations = HomeConversationsOverview(
            total = conversationsTotal,
            messagesTotal = messagesTotal,
            createdLast30Days = conversationsCreatedLast30Days,
            messagesLast30Days = messagesLast30Days,
            createdTrend = window.denseTrend(conversationsCreatedTrend90),
            messagesTrend = window.denseTrend(messagesTrend90),
        ),
        games = HomeGamesOverview(
            comingSoon = true,
            total = gamesTotal,
            last30Days = gamesLast30Days,
            trend = window.denseTrend(gamesFinishedTrend90),
        ),
    ),
    recentContent = HomeRecentContent(
        words = emptyList(),
        conversations = emptyList(),
    ),
)

fun HomeWindow.toActivity(days: List<HomeActivityDay>): HomeActivityPerDay = HomeActivityPerDay(
    year = year,
    days = days.mergeByDate(),
)

fun HomeWindow.denseTrend(sparse: List<HomeActivityDay>): List<HomeActivityDay> {
    val counts = sparse.associate { it.date to it.count }
    val start = today.minusDays((HOME_TREND_DAY_COUNT - 1).toLong())

    return (0 until HOME_TREND_DAY_COUNT).map { offset ->
        val date = start.plusDays(offset.toLong())
        val key = trendDateFormatter.format(date)
        HomeActivityDay(
            date = key,
            count = counts[key] ?: 0L,
        )
    }
}

fun Map<WordType, Long>.omitZeros(): Map<WordType, Long> {
    val counts = linkedMapOf<WordType, Long>()
    for (type in WordType.entries) {
        val count = this[type] ?: 0L
        if (count > 0L) {
            counts[type] = count
        }
    }
    return counts
}

fun List<HomeActivityDay>.mergeByDate(): List<HomeActivityDay> =
    groupBy { it.date }
        .map { (date, rows) ->
            HomeActivityDay(
                date = date,
                count = rows.sumOf { it.count },
            )
        }
        .filter { it.count > 0L }
        .sortedBy { it.date }
