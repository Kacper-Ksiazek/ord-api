package com.ord.features.conversation.models.conversation_user_message_feedback.jsonb

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageErrorType
import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageMistakeSeverity

data class ConversationMessageMistake(
    val phrase: String,
    val severity: ConversationMessageMistakeSeverity,
    val errorType: ConversationMessageErrorType,
    val explanation: String,
    val correctForm: String
)
