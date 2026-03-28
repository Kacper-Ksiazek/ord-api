package com.ord.features.conversation.models.conversation_message

import com.ord.features.conversation.models.conversation.ConversationEntity
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("conversation_messages")
data class ConversationMessageEntity(
    @Id
    val id: UUID? = null,

    val content: String,
    val messageOrder: Int,
    val sender: ConversationMessageSender,

    val conversationId: UUID,

    val createdAt: Instant = Instant.now(),
)