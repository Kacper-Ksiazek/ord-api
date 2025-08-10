package com.ord.features.conversation.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ConversationRepository : UserResourceRepository<ConversationEntity> {
    @Query(
        """
        SELECT c.topic
        FROM ConversationEntity c
        WHERE c.user.id = :userId
          AND c.goal = :goal
          AND c.language = :language
        ORDER BY c.createdAt DESC
        LIMIT :limit
        """
    )
    fun findRecentTopics(
        @Param("userId") userId: UUID,
        @Param("goal") goal: ConversationGoal,
        @Param("language") language: LanguageName,
        @Param("limit") limit: Int
    ): List<String>
}