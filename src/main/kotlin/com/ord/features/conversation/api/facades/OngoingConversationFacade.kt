package com.ord.features.conversation.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.models.dto.ConversationMessageDTO
import com.ord.features.conversation.models.entities.ConversationEntity
import reactor.core.publisher.Flux
import java.util.UUID

interface OngoingConversationFacade {
    /**
     * Initializes conversation by either AI or USER
     */
    fun initializeConversation()

    /**
     * Calls AI API to request a message in conversation from the AI
     */
    fun requestAIMessage(
        user: UserEntity,
        conversationId: UUID,
        lastestUserMessage: String
    ): Flux<String>

    /**
     * Performs a grammar and style review of a single user message, independently
     * of the current conversation context.
     */
    fun saveUserMessageAndGetFeedback()
}