package com.ord.features.conversation.api.requests

import java.util.UUID

/**
 * Request to generate learning tips from the latest AI message in a conversation.
 * The system automatically identifies the most recent AI message that doesn't have learning tips yet.
 *
 * @property conversationId The conversation to analyze
 */
data class GetLearningTipsForAIMessageRequest(
    val conversationId: UUID
)
