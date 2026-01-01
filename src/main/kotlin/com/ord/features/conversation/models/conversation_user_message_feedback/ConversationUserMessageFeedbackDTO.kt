package com.ord.features.conversation.models.conversation_user_message_feedback

import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Mistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Strength
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Suggestion
import java.util.UUID

data class ConversationUserMessageFeedbackDTO(
    val id: UUID,

    val tutorComment: String,

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,
    val registerAppropriate: Boolean,

    val mistakes: Set<Mistake>,
    val strengths: Set<Strength>,
    val suggestions: Set<Suggestion>,

    val messageId: UUID,
)
