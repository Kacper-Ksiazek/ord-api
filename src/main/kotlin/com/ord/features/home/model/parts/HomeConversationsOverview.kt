package com.ord.features.home.model.parts

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Conversation card: totals and 90-day sparklines")
data class HomeConversationsOverview(
    @Schema(description = "All conversations in the selected language", example = "3")
    val total: Long,

    @Schema(description = "All messages in those conversations, from the user and the AI", example = "28")
    val messagesTotal: Long,

    @Schema(description = "Conversations created in the current UTC calendar month, including today", example = "1")
    val createdLast30Days: Long,

    @Schema(description = "Messages created in the current UTC calendar month, from the user and the AI", example = "8")
    val messagesLast30Days: Long,

    @Schema(description = "Conversations created per UTC day for the last 90 days, including zeros")
    val createdTrend: List<HomeActivityDay>,

    @Schema(description = "Messages created per UTC day for the last 90 days, including zeros")
    val messagesTrend: List<HomeActivityDay>,
)
