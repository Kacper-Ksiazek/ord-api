package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageMistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageStrength
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageSuggestion

data class ReviewedUserConversationMessage(
    val sabotage: String? = null,

    val tutorComment: String,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,
    val registerAppropriate: Boolean,

    val mistakes: Set<ConversationMessageMistake>,
    val strengths: Set<ConversationMessageStrength>,

    val suggestions: Set<ConversationMessageSuggestion>
)
