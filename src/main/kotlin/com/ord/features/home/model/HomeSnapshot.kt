package com.ord.features.home.model

import com.ord.core.word.models.word.enums.WordType

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
)
