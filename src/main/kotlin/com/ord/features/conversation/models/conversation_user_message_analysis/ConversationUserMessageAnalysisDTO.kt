package com.ord.features.conversation.models.conversation_user_message_analysis

import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageMistake
import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageStrength
import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageSuggestion
import java.util.UUID

/**
 * Post-persistence representation of a user message analysis record from the database.
 * Unlike [com.ord.features.conversation.api.facades.helpers.ai_responses.ConversationUserMessageAnalysisPayload], this includes persisted identifiers.
 */
data class ConversationUserMessageAnalysisDTO(
    val id: UUID,

    val isSabotage: Boolean,

    val tutorComment: String,

    val grammar: Int,
    val vocabulary: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,

    val mistakes: Set<ConversationMessageMistake>,
    val strengths: Set<ConversationMessageStrength>,
    val suggestions: Set<ConversationMessageSuggestion>,

    val messageId: UUID,
    val correctedMessage: String?,
)
