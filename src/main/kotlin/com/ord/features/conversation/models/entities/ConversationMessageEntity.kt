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
    var id: UUID? = null,

    @Column("message_order")
    var messageOrder: Int,

    @Column("sender")
    var sender: ConversationMessageSender,

    @Column("content")
    var content: String,

    @Column("conversation_id")
    var conversationId: UUID,

    @Column("feedback_id")
    var feedbackId: UUID? = null,

    @Column("created_at")
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    var updatedAt: Instant = Instant.now(),

    @Transient
    var feedback: ConversationUserMessageFeedbackEntity? = null
)
