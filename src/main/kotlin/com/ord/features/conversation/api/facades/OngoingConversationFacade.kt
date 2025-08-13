package com.ord.features.conversation.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import java.util.*

interface OngoingConversationFacade {
    /**
     * Make AI to initialize a conversation with the user.
     */
    fun initializeConversationByAI(
        user: UserEntity,
        conversationId: UUID,
    ): Flux<String>

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
    fun saveUserMessageAndGetFeedback(
        user: UserEntity,
        conversationId: UUID,
        userMessage: String,
        latestAIMessage: String? = null,
        /**
         * Technically, it could be computed from DB by taking the amount of messages, but in
         * practice, for latency reasons, it is better to pass it from the client directly.
         */
        messageOrder: Int
    ): ResponseEntity<ReviewedUserConversationMessage>
}