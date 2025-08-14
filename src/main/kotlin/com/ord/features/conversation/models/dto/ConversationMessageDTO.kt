package com.ord.features.conversation.models.dto

import com.ord.features.conversation.models.enums.ConversationMessageSender
import java.time.Instant
import java.util.*

data class ConversationMessageDTO(
    val id: UUID,

    val messageOrder: Int,
    val sender: ConversationMessageSender,
    val content: String,

    val feedback: ConversationUserMessageFeedbackDTO? = null,

    val createdAt: Instant = Instant.now()
)
