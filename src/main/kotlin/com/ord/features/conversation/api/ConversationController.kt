package com.ord.features.conversation.api

import com.ord.core.auth.security.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.ConversationCRUDFacade
import com.ord.features.conversation.api.facades.ConversationTopicFacade
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/conversations")
class ConversationController(
    val conversationTopicFacade: ConversationTopicFacade,
    val conversationCRUDFacade: ConversationCRUDFacade,
) {
    //
    // AUXILIARY ENDPOINTS - not directly related to any conversation
    //
    @PostMapping("/suggest-topics", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun suggestTopic(
        @AuthenticatedUser user: UserEntity,
        @RequestBody body: SuggestConversationTopicRequest
    ) = conversationTopicFacade.suggestTopics(user, body)

    //
    // CRUD ENDPOINTS
    //
    @PostMapping("/")
    fun createConversation(
        @AuthenticatedUser user: UserEntity,
        @RequestBody body: CreateConversationRequest
    ) = conversationCRUDFacade.createConversation(user, body)
}