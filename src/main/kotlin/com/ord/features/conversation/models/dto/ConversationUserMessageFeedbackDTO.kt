package com.ord.features.conversation.models.dto

import java.util.*

data class ConversationUserMessageFeedbackDTO(
    val id: UUID,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,

    val suggestedAnswer: String? = null,
    val comment: String? = null,
)
