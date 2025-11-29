package com.ord.features.conversation.repositories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation.ConversationEntity
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.dto.RecentConversationInfo
import com.ord.shared.repositories.UserResourceRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ConversationRepositoryCustomMethods {
    fun findRecentTopics(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int
    ): Flux<String>

    fun findRecentConversationsInfo(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int
    ): Flux<RecentConversationInfo>

    fun findByIdOrFailWithMessages(
        id: UUID,
        userId: UUID
    ): Mono<ConversationDTO>
}


interface ConversationRepository :
    UserResourceRepository<ConversationEntity>,
    ConversationRepositoryCustomMethods