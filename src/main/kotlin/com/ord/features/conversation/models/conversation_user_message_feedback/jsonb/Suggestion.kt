package com.ord.features.conversation.models.conversation_user_message_feedback.jsonb

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.SuggestionType

data class Suggestion(
    val original: String,
    val suggestionType: SuggestionType,
    val alternatives: Set<String>,
    val explanation: String
)
