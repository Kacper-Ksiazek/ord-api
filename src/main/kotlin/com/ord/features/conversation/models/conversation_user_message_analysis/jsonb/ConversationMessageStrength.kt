package com.ord.features.conversation.models.conversation_user_message_analysis.jsonb

import com.ord.features.conversation.models.conversation_user_message_analysis.enums.ConversationMessageStrengthType

data class ConversationMessageStrength(
    val phrase: String,
    val strengthType: ConversationMessageStrengthType,
    val explanation: String
)
