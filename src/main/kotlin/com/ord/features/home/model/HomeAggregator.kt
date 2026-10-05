package com.ord.features.home.model

import com.ord.core.word.models.word.enums.WordType
import com.ord.features.home.model.parts.HomeActivityDay
import com.ord.features.home.model.parts.HomeActivityPerDay
import com.ord.features.home.model.parts.HomeConversationsOverview
import com.ord.features.home.model.parts.HomeGamesOverview
import com.ord.features.home.model.parts.HomeOverviews
import com.ord.features.home.model.parts.HomeRecentContent
import com.ord.features.home.model.parts.HomeWordsOverview
import java.time.format.DateTimeFormatter

object HomeAggregator {
    const val TREND_DAY_COUNT: Int = 90

    private val trendDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun empty(window: HomeWindow): HomeResponse = HomeResponse(
        overviews = overviews(
            window = window,
            wordsTotal = 0,
            wordsAddedLast30Days = 0,
            wordsByType = emptyMap(),
            conversationsTotal = 0,
            messagesTotal = 0,
            conversationsCreatedLast30Days = 0,
            messagesLast30Days = 0,
            gamesTotal = 0,
            gamesLast30Days = 0,
            wordsAdded = emptyList(),
            conversationsCreated = emptyList(),
            messages = emptyList(),
            gamesFinished = emptyList(),
        ),
        activityPerDay = HomeActivityPerDay(
            year = window.year,
            days = emptyList(),
        ),
        recentContent = HomeRecentContent(
            words = emptyList(),
            conversations = emptyList(),
        ),
    )

    fun assemble(window: HomeWindow, snapshot: HomeSnapshot): HomeResponse = HomeResponse(
        overviews = overviews(
            window = window,
            wordsTotal = snapshot.wordsTotal,
            wordsAddedLast30Days = snapshot.wordsAddedLast30Days,
            wordsByType = countsByType(snapshot.wordsByType),
            conversationsTotal = snapshot.conversationsTotal,
            messagesTotal = snapshot.messagesTotal,
            conversationsCreatedLast30Days = snapshot.conversationsCreatedLast30Days,
            messagesLast30Days = snapshot.messagesLast30Days,
            gamesTotal = snapshot.gamesTotal,
            gamesLast30Days = snapshot.gamesLast30Days,
            wordsAdded = snapshot.wordsAddedTrend90,
            conversationsCreated = snapshot.conversationsCreatedTrend90,
            messages = snapshot.messagesTrend90,
            gamesFinished = snapshot.gamesFinishedTrend90,
        ),
        activityPerDay = HomeActivityPerDay(
            year = window.year,
            days = activityDays(snapshot.activityDays),
        ),
        recentContent = HomeRecentContent(
            words = emptyList(),
            conversations = emptyList(),
        ),
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

    private fun overviews(
        window: HomeWindow,
        wordsTotal: Long,
        wordsAddedLast30Days: Long,
        wordsByType: Map<WordType, Long>,
        conversationsTotal: Long,
        messagesTotal: Long,
        conversationsCreatedLast30Days: Long,
        messagesLast30Days: Long,
        gamesTotal: Long?,
        gamesLast30Days: Long?,
        wordsAdded: List<HomeActivityDay>,
        conversationsCreated: List<HomeActivityDay>,
        messages: List<HomeActivityDay>,
        gamesFinished: List<HomeActivityDay>,
    ): HomeOverviews = HomeOverviews(
        words = HomeWordsOverview(
            total = wordsTotal,
            addedLast30Days = wordsAddedLast30Days,
            byType = wordsByType,
            trend = denseTrendDays(window, wordsAdded),
        ),
        conversations = HomeConversationsOverview(
            total = conversationsTotal,
            messagesTotal = messagesTotal,
            createdLast30Days = conversationsCreatedLast30Days,
            messagesLast30Days = messagesLast30Days,
            createdTrend = denseTrendDays(window, conversationsCreated),
            messagesTrend = denseTrendDays(window, messages),
        ),
        games = HomeGamesOverview(
            comingSoon = true,
            total = gamesTotal,
            last30Days = gamesLast30Days,
            trend = denseTrendDays(window, gamesFinished),
        ),
    )
}
