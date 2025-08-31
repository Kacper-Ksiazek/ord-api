package com.ord.features.conversation.models.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("conversation_user_message_feedback")
data class ConversationUserMessageFeedbackEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,

    val comment: String? = null,
    val suggestedAnswer: String? = null,

    val createdAt: Instant = Instant.now(),
)

