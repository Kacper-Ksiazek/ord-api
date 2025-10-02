package com.ord.features.conversation.services.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.enums.ConversationType
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
        goal: ConversationType,
        language: LanguageName,
        limit: Int
    ): Flux<String> {
        return conversationRepository.findRecentTopics(
            userId = userId,
            goal = goal,
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
}
