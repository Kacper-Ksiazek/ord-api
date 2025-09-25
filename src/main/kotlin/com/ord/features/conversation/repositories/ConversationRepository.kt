package com.ord.features.conversation.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ConversationRepositoryCustomMethods {
    fun findRecentTopics(
        userId: UUID,
        goal: ConversationGoal,
        language: LanguageName,
        limit: Int
    ): Flux<String>


    fun findByIdOrFailWithMessages(
        id: UUID,
        userId: UUID
    ): Mono<ConversationDTO>
}


interface ConversationRepository :
    UserResourceRepository<ConversationEntity>,
    ReactiveCrudRepository<ConversationEntity, UUID>,
    ConversationRepositoryCustomMethods