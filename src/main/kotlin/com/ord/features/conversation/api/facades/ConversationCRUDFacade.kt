package com.ord.features.conversation.api.facades

import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation.ConversationListFilters
import com.ord.features.conversation.models.conversation.ConversationSummaryDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface ConversationCRUDFacade {
    fun createConversation(
        userId: UUID,
        body: CreateConversationRequest,
    ): Mono<ResponseEntity<ConversationDTO>>


    fun getManyConversations(
        userId: UUID,
        filters: ConversationListFilters,
    ): Mono<ResponseEntity<List<ConversationSummaryDTO>>>


    fun getConversationById(
        userId: UUID,
        conversationId: UUID,
    ): Mono<ResponseEntity<ConversationDTO>>


    fun deleteConversation(
        userId: UUID,
        conversationId: UUID,
    ): Mono<ResponseEntity<Unit>>
}