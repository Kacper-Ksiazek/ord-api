package com.ord.features.conversation.models.conversation_user_message_feedback

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("conversation_user_message_feedback")
data class ConversationUserMessageFeedbackEntity(
    @Id
    val id: UUID? = null,

    val tutorComment: String,
    val correctedMessage: String,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,
    val registerAppropriate: Boolean,

    val mistakes: Json, // JSONB - Set<ConversationMessageMistake>
    val strengths: Json, // JSONB - Set<ConversationMessageStrength>
    val suggestions: Json, // JSONB - Set<ConversationMessageSuggestion>

    val messageId: UUID,

    val createdAt: Instant = Instant.now(),
)