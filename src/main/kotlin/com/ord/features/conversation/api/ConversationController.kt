package com.ord.features.conversation.api

import com.ord.core.auth.security.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.ConversationCRUDFacade
import com.ord.features.conversation.api.facades.ConversationTopicFacade
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
    @GetMapping("/")
    fun getConversations(
        @AuthenticatedUser user: UserEntity
    ) = conversationCRUDFacade.getManyConversations(user)


    @GetMapping("/{conversationId}")
    fun getConversations(
        @AuthenticatedUser user: UserEntity,
        @PathVariable conversationId: UUID,
    ) = conversationCRUDFacade.getConversationById(user, conversationId)


    @PostMapping("/")
    fun createConversation(
        @AuthenticatedUser user: UserEntity,
        @RequestBody body: CreateConversationRequest
    ) = conversationCRUDFacade.createConversation(user, body)


    @DeleteMapping("/{conversationId}")
    fun deleteConversation(
        @AuthenticatedUser user: UserEntity,
        @PathVariable conversationId: UUID,
    ) = conversationCRUDFacade.deleteConversation(user, conversationId)
}