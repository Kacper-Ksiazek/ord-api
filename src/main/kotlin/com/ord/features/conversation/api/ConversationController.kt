package com.ord.features.conversation.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.conversation.api.facades.ConversationCRUDFacade
import com.ord.features.conversation.api.facades.ConversationAuxiliaryFacade
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.api.requests.GenerateAIInterlocutorDataRequest
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import com.ord.features.conversation.models.dto.ConversationDTO
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/conversations")
class ConversationController(
    val conversationTopicFacade: ConversationAuxiliaryFacade,
    val conversationCRUDFacade: ConversationCRUDFacade,
) {
    //
    // AUXILIARY ENDPOINTS - not directly related to any conversation
    //

    @PostMapping("/suggest-topics", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun suggestTopic(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: SuggestConversationTopicRequest
    ) = conversationTopicFacade.suggestTopics(
        userId = user.id,
        body = body
    )

    @PostMapping("/suggest-ai-interlocutor")
    fun generateAIInterlocutorData(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: GenerateAIInterlocutorDataRequest
    ) = conversationTopicFacade.generateAIInterlocutorData(
        userId = user.id,
        body = body
    )

    //
    // CRUD ENDPOINTS
    //
    @GetMapping("/")
    fun getConversations(
        @AuthenticatedUser user: UserDTO
    ): Mono<ResponseEntity<List<ConversationDTO>>> = conversationCRUDFacade.getManyConversations(
        userId = user.id
    )


    @GetMapping("/{conversationId}")
    fun getConversationById(
        @AuthenticatedUser user: UserDTO,
        @PathVariable conversationId: UUID,
    ): Mono<ResponseEntity<ConversationDTO>> = conversationCRUDFacade.getConversationById(
        conversationId = conversationId,
        userId = user.id
    )


    @PostMapping("/")
    fun createConversation(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateConversationRequest
    ): Mono<ResponseEntity<ConversationDTO>> = conversationCRUDFacade.createConversation(
        userId = user.id,
        body = body
    )


    @DeleteMapping("/{conversationId}")
    fun deleteConversation(
        @AuthenticatedUser user: UserDTO,
        @PathVariable conversationId: UUID,
    ): Mono<ResponseEntity<Unit>> = conversationCRUDFacade.deleteConversation(
        conversationId = conversationId,
        userId = user.id
    )
}