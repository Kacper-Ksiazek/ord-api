package com.ord.features.conversation.models.conversation_user_message_feedback.jsonb

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.StrengthType

data class Strength(
    val phrase: String,
    val strengthType: StrengthType,
    val explanation: String
)
