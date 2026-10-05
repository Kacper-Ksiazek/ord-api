package com.ord.features.home.model

import com.ord.core.word.models.word.enums.WordType
import com.ord.features.home.model.parts.HomeActivityDay

data class HomeSnapshot(
    val wordsTotal: Long,
    val wordsAddedLast30Days: Long,
    val wordsByType: Map<WordType, Long>,
    val conversationsTotal: Long,
    val conversationsCreatedLast30Days: Long,
    val messagesTotal: Long,
    val messagesLast30Days: Long,
    val gamesTotal: Long?,
    val gamesLast30Days: Long?,
    val activityDays: List<HomeActivityDay>,
    val wordsAddedTrend90: List<HomeActivityDay>,
    val conversationsCreatedTrend90: List<HomeActivityDay>,
    val messagesTrend90: List<HomeActivityDay>,
    val gamesFinishedTrend90: List<HomeActivityDay>,
)
