package com.ord.controllers.conversations.helpers.unsafe_dtos

data class UnsafeSuggestConversationTopicRequest(
    val clueFromUser: String? = null,
    val conversationType: String? = null,
    val language: String? = null,
    val excludeTopics: List<String>? = null,
)
