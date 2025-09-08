package com.ord.features.conversation.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface ConversationRepository : UserResourceRepository<ConversationEntity> {
    @Query(
        """
        SELECT c.topic
        FROM conversations c
        WHERE c.user_id = :userId
            AND c.goal = :goal
            AND c.language = :language
        ORDER BY c.created_at DESC
        LIMIT :limit
        """
    )
    fun findRecentTopics(
        @Param("userId") userId: UUID,
        @Param("goal") goal: ConversationGoal,
        @Param("language") language: LanguageName,
        @Param("limit") limit: Int
    ): Flux<String>


    @Query(
        """
        SELECT 
            c.*,
            m.id as message_id,
            m.conversation_id,
            m.sender,
            m.content,
            m.message_order,
            m.created_at as message_created_at
        FROM conversations c
        LEFT JOIN conversation_messages m ON c.id = m.conversation_id
        WHERE c.user_id = :userId
            AND c.id = :id
        ORDER BY m.message_order
        """
    )
    fun findByIdOrFailWithMessages(
        @Param("id") id: UUID,
        @Param("userId") userId: UUID
    ): Mono<ConversationEntity>
}