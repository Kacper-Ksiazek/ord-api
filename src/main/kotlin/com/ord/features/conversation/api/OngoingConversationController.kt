package com.ord.features.conversation.api

import com.ord.core.auth.security.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/conversations/ongoing")
class OngoingConversationController(
    val ongoingConversationFacade: OngoingConversationFacade
) {
    @PostMapping("/initialize-by-ai", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun initializeConversationByAI(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestParam(required = true, name = "conversationId") conversationId: UUID,
    ) = ongoingConversationFacade.initializeConversationByAI(user, conversationId)


    @PostMapping("/request-ai-message", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun requestAIMessage(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: CreateAIConversationMessageRequest
    ) = ongoingConversationFacade.requestAIMessage(user, body)


    @PostMapping("/handle-user-message")
    fun handleUserMessage(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: ReviewUserConversationMessageRequest
    ) = ongoingConversationFacade.saveUserMessageAndGetFeedback(user, body)
}