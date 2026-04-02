package com.ord.features.conversation.models.conversation_activity

import com.fasterxml.jackson.annotation.JsonValue

data class ConversationActivityOverviewDTO(
    val periodLabel: String,
    val messagesTotal: Long,
    val conversationsTotal: Long,
    val heatmap: List<HeatmapDayDTO>,
    val messagesByWeek: List<ActivityWeekPointDTO>,
    val conversationsByWeek: List<ActivityWeekPointDTO>,
)

data class HeatmapDayDTO(
    val count: Long,
    val date: String,
    val percentile: HeatmapPercentile,
)

enum class HeatmapPercentile(@JsonValue val label: String) {
    P0("p0"),
    P20("p20"),
    P40("p40"),
    P60("p60"),
    P80("p80"),
}

data class ActivityWeekPointDTO(
    val weekRange: String,
    val count: Long,
)
