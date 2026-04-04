package com.ord.features.conversation.models.conversation_user_message_analysis

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("conversation_user_message_analysis")
data class ConversationUserMessageAnalysisEntity(
    @Id
    val id: UUID? = null,

    val isSabotage: Boolean,

    val tutorComment: String,
    val correctedMessage: String?,

    val grammar: Int,
    val vocabulary: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,

    val mistakes: Json, // JSONB - Set<ConversationMessageMistake>
    val strengths: Json, // JSONB - Set<ConversationMessageStrength>
    val suggestions: Json, // JSONB - Set<ConversationMessageSuggestion>

    val messageId: UUID,

    val createdAt: Instant = Instant.now(),
)
