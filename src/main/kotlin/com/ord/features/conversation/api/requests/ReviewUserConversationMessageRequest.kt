package com.ord.features.conversation.api.requests

import java.util.UUID

data class ReviewUserConversationMessageRequest(
    val conversationId: UUID,
    val message: String,
    val messageOrder: Int,
)