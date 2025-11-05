package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.AlternativeExpression
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Mistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.VocabularyEnrichment

data class ReviewedUserConversationMessage(
    val sabotage: String? = null,

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

    val culturalNote: String? = null
)
