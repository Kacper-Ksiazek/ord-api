package com.ord.features.conversation.models.conversation_user_message_feedback

import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.AlternativeExpression
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Mistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.VocabularyEnrichment
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

    val mistakes: Set<Mistake>,
    val strengthsIdentified: Set<String>,
    val vocabularyEnrichment: Set<VocabularyEnrichment>,
    val alternativeExpressions: Set<AlternativeExpression>,
    val culturalNote: String? = null,

    val messageId: UUID,

    val createdAt: Instant = Instant.now(),
)