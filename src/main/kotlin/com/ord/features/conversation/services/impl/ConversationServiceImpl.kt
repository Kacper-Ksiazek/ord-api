package com.ord.features.conversation.services.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation.ConversationEntity
import com.ord.features.conversation.models.conversation.ConversationListFilters
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.dto.RecentConversationInfo
import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.features.conversation.services.ConversationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class ConversationServiceImpl(
    private val conversationRepository: ConversationRepository
) : ConversationService {
    override val repository: ConversationRepository = conversationRepository

    override fun findRecentTopics(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int
    ): Flux<String> {
        return conversationRepository.findRecentTopics(
            userId = userId,
            type = type,
            language = language,
            limit = limit
        )
    }

    override fun findRecentConversationsInfo(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int
    ): Flux<RecentConversationInfo> {
        return conversationRepository.findRecentConversationsInfo(
            userId = userId,
            type = type,
            language = language,
            limit = limit
        )
    }

    override fun findByIdOrFailWithMessages(
        id: UUID,
        userId: UUID
    ): Mono<ConversationDTO> {
        return conversationRepository
            .findByIdOrFailWithMessages(id, userId)
            .switchIfEmpty(Mono.error(NotFoundException("Conversation with id $id not found")))
    }

    override fun findAllWithFilters(userId: UUID, filters: ConversationListFilters): Flux<ConversationEntity> =
        conversationRepository.findAllWithFilters(userId, filters)
}
