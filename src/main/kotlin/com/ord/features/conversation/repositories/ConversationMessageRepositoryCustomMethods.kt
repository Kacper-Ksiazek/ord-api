package com.ord.features.conversation.repositories

import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface ConversationMessageRepositoryCustomMethods {
    /**
     * Finds the most recent AI message in a conversation that doesn't have learning tips generated yet.
     *
     * @param conversationId The conversation to search in
     * @return Mono of the latest AI message without learning tips, or empty Mono if none found
     */
    fun findLatestAIMessageWithoutLearningTips(conversationId: UUID): Mono<ConversationMessageEntity>
}
