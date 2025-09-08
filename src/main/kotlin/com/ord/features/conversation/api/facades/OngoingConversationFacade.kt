package com.ord.features.conversation.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
import com.ord.features.conversation.models.entities.ConversationEntity
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface OngoingConversationFacade {
    /**
     * Make AI to initialize a conversation with the user.
     */
    fun initializeConversationByAI(conversation: ConversationEntity): Flux<String>

    /**
     * Calls AI API to request a message in conversation from the AI
     */
    fun requestAIMessage(
        user: UserEntity,
        body: CreateAIConversationMessageRequest
    ): Flux<String>

    /**
     * Performs a grammar and style review of a single user message, independently
     * of the current conversation context.
     */
    fun saveUserMessageAndGetFeedback(
        user: UserEntity,
        body: ReviewUserConversationMessageRequest
    ): Mono<ResponseEntity<ReviewedUserConversationMessage>>
}