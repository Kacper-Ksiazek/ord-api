package com.ord.features.conversation.models.conversation_user_message_feedback.jsonb

data class VocabularyEnrichment(
    val original: String,
    val suggestion: String,
    val explanation: String
)