package com.ord.features.home.model

import com.ord.core.word.models.word.enums.WordType
import java.time.format.DateTimeFormatter

object HomeAggregator {
    const val TREND_DAY_COUNT: Int = 90

    private val trendDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    fun empty(window: HomeWindow): HomeResponse = HomeResponse(
        words = HomeWordsSection(
            total = 0,
            addedLast30Days = 0,
            byType = emptyMap(),
        ),
        conversations = HomeConversationsSection(
            total = 0,
            messagesTotal = 0,
            createdLast30Days = 0,
            messagesLast30Days = 0,
        ),
        games = HomeGamesSection(
            comingSoon = true,
            total = 0,
            last30Days = 0,
        ),
        trends = trendsSection(window, emptyList(), emptyList(), emptyList(), emptyList()),
        activity = HomeActivitySection(
            year = window.year,
            days = emptyList(),
        ),
    )

    fun assemble(window: HomeWindow, snapshot: HomeSnapshot): HomeResponse = HomeResponse(
        words = HomeWordsSection(
            total = snapshot.wordsTotal,
            addedLast30Days = snapshot.wordsAddedLast30Days,
            byType = countsByType(snapshot.wordsByType),
        ),
        conversations = HomeConversationsSection(
            total = snapshot.conversationsTotal,
            messagesTotal = snapshot.messagesTotal,
            createdLast30Days = snapshot.conversationsCreatedLast30Days,
            messagesLast30Days = snapshot.messagesLast30Days,
        ),
        games = HomeGamesSection(
            comingSoon = true,
            total = snapshot.gamesTotal,
            last30Days = snapshot.gamesLast30Days,
        ),
        trends = trendsSection(
            window = window,
            wordsAdded = snapshot.wordsAddedTrend90,
            conversationsCreated = snapshot.conversationsCreatedTrend90,
            messages = snapshot.messagesTrend90,
            gamesFinished = snapshot.gamesFinishedTrend90,
        ),
        activity = HomeActivitySection(
            year = window.year,
            days = activityDays(snapshot.activityDays),
        ),
    )

    fun trendsSection(
        window: HomeWindow,
        wordsAdded: List<HomeActivityDay>,
        conversationsCreated: List<HomeActivityDay>,
        messages: List<HomeActivityDay>,
        gamesFinished: List<HomeActivityDay>,
    ): HomeTrendsSection = HomeTrendsSection(
        wordsAdded = denseTrendDays(window, wordsAdded),
        conversationsCreated = denseTrendDays(window, conversationsCreated),
        messages = denseTrendDays(window, messages),
        gamesFinished = denseTrendDays(window, gamesFinished),
    )

    fun denseTrendDays(window: HomeWindow, sparse: List<HomeActivityDay>): List<HomeActivityDay> {
        val counts = sparse.associate { it.date to it.count }
        val start = window.today.minusDays((TREND_DAY_COUNT - 1).toLong())

        return (0 until TREND_DAY_COUNT).map { offset ->
            val date = start.plusDays(offset.toLong())
            val key = trendDateFormatter.format(date)
            HomeActivityDay(
                date = key,
                count = counts[key] ?: 0L,
            )
        }
    }

    fun countsByType(raw: Map<WordType, Long>): Map<WordType, Long> {
        val counts = linkedMapOf<WordType, Long>()
        for (type in WordType.entries) {
            val count = raw[type] ?: 0L
            if (count > 0L) {
                counts[type] = count
            }
        }
        return counts
    }

    fun activityDays(raw: List<HomeActivityDay>): List<HomeActivityDay> =
        raw
            .groupBy { it.date }
            .map { (date, rows) ->
                HomeActivityDay(
                    date = date,
                    count = rows.sumOf { it.count },
                )
            }
            .filter { it.count > 0L }
            .sortedBy { it.date }
}
