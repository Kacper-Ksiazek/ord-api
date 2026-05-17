package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageMistake
import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageStrength
import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageSuggestion

/**
 * Pre-persistence AI analysis payload returned by OpenAI structured output.
 * Contains transient fields (e.g. isSabotage) that are not persisted to the database.
 */
data class ConversationUserMessageAnalysisPayload(
    val isSabotage: Boolean,

    val correctedMessage: String?,
    val tutorComment: String,

    val grammar: Int,
    val vocabulary: Int,
    val naturalness: Int,
    val coherenceWithContext: Int,

    val mistakes: Set<ConversationMessageMistake>,
    val strengths: Set<ConversationMessageStrength>,

    val suggestions: Set<ConversationMessageSuggestion>
)
