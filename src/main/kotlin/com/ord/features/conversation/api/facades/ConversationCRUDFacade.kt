package com.ord.features.conversation.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.models.dto.ConversationDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface ConversationCRUDFacade {
    fun createConversation(
        user: UserEntity,
        body: CreateConversationRequest,
    ): Mono<ResponseEntity<ConversationDTO>>

    fun getManyConversations(
        user: UserEntity,
    ): Mono<ResponseEntity<List<ConversationDTO>>>

    fun getConversationById(
        user: UserEntity,
        conversationId: UUID,
    ): Mono<ResponseEntity<ConversationDTO>>

    fun deleteConversation(
        user: UserEntity,
        conversationId: UUID,
    ): Mono<ResponseEntity<Unit>>
}