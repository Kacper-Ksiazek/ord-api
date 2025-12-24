package com.ord.features.conversation.api.requests

import java.util.UUID

/**
 * Request to generate learning tips from an AI message.
 * @property conversationId The conversation containing the AI message
 * @property messageId The specific AI message to analyze
 */
data class GetLearningTipsForAIMessageRequest(
    val conversationId: UUID,
    val messageId: UUID
)
