package com.ord.features.conversation.models.conversation_message

import com.ord.features.conversation.models.conversation_user_message_feedback.ConversationUserMessageFeedbackDTO
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import java.time.Instant
import java.util.UUID

data class ConversationMessageDTO(
    val id: UUID,

    val messageOrder: Int,
    val sender: ConversationMessageSender,
    val content: String,

    val feedback: ConversationUserMessageFeedbackDTO? = null,

    val createdAt: Instant = Instant.now()
)