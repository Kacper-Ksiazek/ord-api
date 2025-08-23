package com.ord.features.conversation.services.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.dto.ConversationMessageDTO
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.features.conversation.models.mappers.ConversationMessageMapper
import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.features.conversation.services.ConversationService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConversationServiceImpl(
    override val repository: ConversationRepository
) : ConversationService {
    override fun findRecentTopics(
        userId: UUID,
        goal: ConversationGoal,
        language: LanguageName,
        limit: Int
    ): List<String> {
        return repository.findRecentTopics(
            userId = userId,
            goal = goal,
            language = language,
            limit = limit
        )
    }

    override fun findByIdOrFailWithMessages(
        id: UUID,
        userId: UUID
    ): ConversationEntity {
        return repository.findByIdOrFailWithMessages(id, userId)
            ?: throw NotFoundException("Conversation with id $id not found")
    }
}
