package com.ord.features.conversation.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/conversations/ongoing")
class OngoingConversationController(
    val ongoingConversationFacade: OngoingConversationFacade
) {
    @PostMapping("/initialize-by-ai", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun initializeConversationByAI(
        @Valid @RequestParam(required = true, name = "conversationId") conversationId: UUID,
        @AuthenticatedUser user: UserDTO,
    ) = ongoingConversationFacade.initializeConversationByAI(
        conversationId = conversationId,
        userId = user.id
    )


    @PostMapping("/request-ai-message", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun requestAIMessage(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateAIConversationMessageRequest
    ) = ongoingConversationFacade.requestAIMessage(user.id, body)


    @PostMapping("/handle-user-message")
    fun handleUserMessage(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ReviewUserConversationMessageRequest
    ): Mono<ResponseEntity<ReviewedUserConversationMessage>> =
        ongoingConversationFacade.saveUserMessageAndGetFeedback(user.id, body)
}