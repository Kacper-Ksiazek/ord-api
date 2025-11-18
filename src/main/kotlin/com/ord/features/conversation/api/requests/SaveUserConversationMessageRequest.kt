package com.ord.features.conversation.api.requests

import java.util.UUID

data class SaveUserConversationMessageRequest(
    val conversationId: UUID,
    val messageId: UUID,
    val content: String,
    val messageOrder: Int
)
