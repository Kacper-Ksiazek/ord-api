package com.ord.features.conversation.models.conversation_user_message_feedback.jsonb

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageStrengthType

data class ConversationMessageStrength(
    val phrase: String,
    val strengthType: ConversationMessageStrengthType,
    val explanation: String
)
