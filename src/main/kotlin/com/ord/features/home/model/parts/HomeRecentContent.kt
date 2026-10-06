package com.ord.features.home.model.parts

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Up to three latest words and conversations for the selected learning language")
data class HomeRecentContent(
    val words: List<HomeRecentWord>,
    val conversations: List<HomeRecentConversation>,
)
