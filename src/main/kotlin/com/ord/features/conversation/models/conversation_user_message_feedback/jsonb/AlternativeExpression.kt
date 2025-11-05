package com.ord.features.conversation.models.conversation_user_message_feedback.jsonb

data class AlternativeExpression(
    val context: String,
    val alternatives: Set<String>,
    val note: String? = null
)