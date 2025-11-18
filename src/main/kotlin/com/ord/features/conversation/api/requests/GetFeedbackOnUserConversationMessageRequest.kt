package com.ord.features.conversation.api.requests

import java.util.UUID

data class ReviewUserConversationMessageRequest(
    val conversationId: UUID,
    val messageId: UUID,
    val messageOrder: Int,
    val latestAIMessage: String? = null,
)