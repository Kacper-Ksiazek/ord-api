package com.ord.features.conversation.models.conversation_user_message_feedback

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("conversation_user_message_feedback")
data class ConversationUserMessageFeedbackEntity(
    @Id
    val id: UUID? = null,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,

    val comment: String? = null,
    val suggestedAnswer: String? = null,

    val messageId: UUID,

    val createdAt: Instant = Instant.now(),
)