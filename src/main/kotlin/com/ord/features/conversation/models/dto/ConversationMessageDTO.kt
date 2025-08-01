package com.ord.features.conversation.models.dto

import com.ord.features.conversation.models.enums.ConversationMessageSender
import java.time.Instant
import java.util.*

data class ConversationMessageDTO(
    val id: UUID = UUID.randomUUID(),

    val sender: ConversationMessageSender,
    val content: String,

    val userId: UUID,
    val conversationId: UUID,
    val feedback: ConversationUserMessageFeedbackDTO? = null,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

