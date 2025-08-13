package com.ord.features.conversation.api

import com.ord.core.auth.security.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
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
        @RequestParam(required = false) conversationId: UUID,
    ) = ongoingConversationFacade.initializeConversationByAI(user, conversationId)


    @PostMapping("/request-ai-message", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun requestAIMessage(
        @AuthenticatedUser user: UserEntity,
        @RequestBody body: CreateAIConversationMessageRequest
    ) = ongoingConversationFacade.requestAIMessage(user, body)


    @PostMapping("/handle-user-message")
    fun handleUserMessage(
        @AuthenticatedUser user: UserEntity,
        @RequestBody body: ReviewUserConversationMessageRequest
    ) = ongoingConversationFacade.saveUserMessageAndGetFeedback(user, body)
}