package com.ord.features.home.model

import com.ord.core.word.models.word.enums.WordType

object HomeAggregator {
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
        activity = HomeActivitySection(
            year = window.year,
            days = activityDays(snapshot.activityDays),
        ),
    )

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
