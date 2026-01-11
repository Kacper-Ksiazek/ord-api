package com.ord.features.conversation.models.conversation_ai_message_learning_tips

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("conversation_ai_message_learning_tips")
data class ConversationAIMessageLearningTipsEntity(
    @Id
    val id: UUID? = null,

    val grammarTips: Json,      // JSONB - Set<AnnotatedGrammarTip>
    val vocabularyTips: Json,   // JSONB - Set<AnnotatedVocabularyTip>
    val phraseTips: Json,       // JSONB - Set<AnnotatedPhraseTip>

    val messageId: UUID,

    val createdAt: Instant = Instant.now(),
)
