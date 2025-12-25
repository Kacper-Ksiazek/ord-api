package com.ord.features.conversation.api.facades

import com.ord.features.conversation.api.facades.helpers.ai_responses.AIMessageLearningTips
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.GetFeedbackOnUserConversationMessageRequest
import com.ord.features.conversation.api.requests.GetLearningTipsForAIMessageRequest
import com.ord.features.conversation.api.requests.SaveUserConversationMessageRequest
import com.ord.features.conversation.models.conversation_message.ConversationMessageDTO
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
     * Saves a user message to a conversation.
     */
    fun saveUserMessage(
        userId: UUID,
        body: SaveUserConversationMessageRequest
    ): Mono<ResponseEntity<ConversationMessageDTO>>

    /**
     * Generates feedback for an existing user message in a conversation.
     */
    fun generateFeedbackForMessage(
        userId: UUID,
        body: GetFeedbackOnUserConversationMessageRequest
    ): Mono<ResponseEntity<ReviewedUserConversationMessage>>

    /**
     * Generates learning tips for an existing AI message in a conversation.
     */
    fun generateLearningTipsForAIMessage(
        userId: UUID,
        body: GetLearningTipsForAIMessageRequest
    ): Mono<ResponseEntity<AIMessageLearningTips>>
}