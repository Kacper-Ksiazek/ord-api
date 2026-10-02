package com.ord.features.home.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Daily counts for the last 90 UTC days ending today, one point per day")
data class HomeTrendsSection(
    @Schema(description = "Words created per day")
    val wordsAdded: List<HomeActivityDay>,

    @Schema(description = "Conversations created per day")
    val conversationsCreated: List<HomeActivityDay>,

    @Schema(description = "Conversation messages created per day")
    val messages: List<HomeActivityDay>,

    @Schema(description = "Finished games recorded per day")
    val gamesFinished: List<HomeActivityDay>,
)
