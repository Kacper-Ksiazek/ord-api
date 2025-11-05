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

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,
    val registerAppropriate: Boolean,

    val mistakes: Json, // JSONB - Set<Mistake>
    val strengthsIdentified: Json, // JSONB - Set<String>
    val vocabularyEnrichment: Json, // JSONB - Set<VocabularyEnrichment>
    val alternativeExpressions: Json, // JSONB - Set<AlternativeExpression>
    val culturalNote: String? = null,

    val messageId: UUID,

    val createdAt: Instant = Instant.now(),
)