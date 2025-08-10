package com.ord.features.conversation.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.models.dto.ConversationDTO
import org.springframework.http.ResponseEntity
import java.util.UUID

interface ConversationCRUDFacade {
    fun createConversation(
        user: UserEntity,
        body: CreateConversationRequest,
    ): ResponseEntity<ConversationDTO>

    fun getManyConversations(
        user: UserEntity,
    ): ResponseEntity<List<ConversationDTO>>

    fun getConversationById(
        user: UserEntity,
        conversationId: UUID,
    ): ResponseEntity<ConversationDTO>

    fun deleteConversation(
        user: UserEntity,
        conversationId: UUID,
    ): ResponseEntity<Unit>
}