package com.ord.features.conversation.api.requests

import java.util.UUID

data class CreateAIConversationMessageRequest(
    val conversationId: UUID,
    val messageOrder: Int,
)