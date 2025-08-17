package com.ord.features.conversation.api.facades.helpers.ai_responses

data class ReviewedUserConversationMessage(
    val sabotage: String? = null,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,

    val comment: String? = null,
    val suggestedAnswer: String? = null
)
