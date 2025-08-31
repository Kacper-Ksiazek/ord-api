package com.ord.features.conversation.models.entities

import com.ord.features.conversation.models.enums.ConversationMessageSender
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("conversation_messages")
data class ConversationMessageEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    val content: String,
    val messageOrder: Int,
    val sender: ConversationMessageSender,

    val conversationId: UUID,
    var feedbackId: UUID? = null,

    val createdAt: Instant = Instant.now(),

    @Transient var feedback: ConversationUserMessageFeedbackEntity? = null,
    @Transient var conversation: ConversationEntity? = null
)
