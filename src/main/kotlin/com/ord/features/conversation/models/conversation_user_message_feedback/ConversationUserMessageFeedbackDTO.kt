package com.ord.features.conversation.models.conversation_user_message_feedback

import java.util.UUID

data class ConversationUserMessageFeedbackDTO(
    val id: UUID,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,

    val comment: String? = null,
    val suggestedAnswer: String? = null,

    val messageId: UUID,
)