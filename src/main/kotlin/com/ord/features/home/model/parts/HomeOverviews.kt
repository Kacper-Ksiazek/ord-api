package com.ord.features.home.model.parts

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Count cards and their sparklines")
data class HomeOverviews(
    val words: HomeWordsOverview,
    val conversations: HomeConversationsOverview,
    val games: HomeGamesOverview,
)
