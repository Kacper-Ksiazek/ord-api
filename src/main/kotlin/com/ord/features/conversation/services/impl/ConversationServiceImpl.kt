package com.ord.features.conversation.services.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.enums.ConversationGoal
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
}
