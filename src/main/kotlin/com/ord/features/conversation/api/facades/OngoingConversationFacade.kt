package com.ord.features.conversation.api.facades

import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
import com.ord.features.conversation.models.conversation.ConversationDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface OngoingConversationFacade {
    /**
     * Make AI to initialize a conversation with the user.
     */
    fun initializeConversationByAI(
        conversationId: UUID,
        userId: UUID,
    ): Flux<String>

    /**
     * Calls AI API to request a message in conversation from the AI
     */
    fun requestAIMessage(
        userId: UUID,
        body: CreateAIConversationMessageRequest
    ): Flux<String>

    /**
     * Performs a grammar and style review of a single user message, independently
     * of the current conversation context.
     */
    fun saveUserMessageAndGetFeedback(
        userId: UUID,
        body: ReviewUserConversationMessageRequest
    ): Mono<ResponseEntity<ReviewedUserConversationMessage>>
}