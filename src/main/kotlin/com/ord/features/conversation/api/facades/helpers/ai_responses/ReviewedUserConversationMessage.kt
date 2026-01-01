package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Mistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Strength
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Suggestion

data class ReviewedUserConversationMessage(
    val sabotage: String? = null,

    val tutorComment: String,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,
    val registerAppropriate: Boolean,

    val mistakes: Set<Mistake>,
    val strengths: Set<Strength>,

    val suggestions: Set<Suggestion>
)
