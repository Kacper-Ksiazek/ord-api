package com.ord.features.conversation.models.conversation_user_message_feedback

import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageMistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageStrength
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.ConversationMessageSuggestion
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

    val mistakes: Set<ConversationMessageMistake>,
    val strengths: Set<ConversationMessageStrength>,
    val suggestions: Set<ConversationMessageSuggestion>,

    val messageId: UUID,
    val correctedMessage: String,
)
