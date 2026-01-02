package com.ord.features.conversation.models.conversation_user_message_feedback.jsonb

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageSuggestionType

data class ConversationMessageSuggestion(
    val original: String,
    val suggestionType: ConversationMessageSuggestionType,
    val alternatives: Set<String>,
    val explanation: String
)
