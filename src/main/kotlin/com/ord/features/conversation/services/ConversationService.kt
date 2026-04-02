package com.ord.features.conversation.services

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation.ConversationEntity
import com.ord.features.conversation.models.conversation.ConversationListFilters
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.conversation_activity.DailyActivityCount
import com.ord.features.conversation.models.dto.RecentConversationInfo
import com.ord.shared.services.UserResourceService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

interface ConversationService : UserResourceService<ConversationEntity> {
    fun findRecentTopics(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int = 10
    ): Flux<String>

    fun findRecentConversationsInfo(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int = 10
    ): Flux<RecentConversationInfo>

    fun findByIdOrFailWithMessages(
        id: UUID,
        userId: UUID
    ): Mono<ConversationDTO>

    fun findAllWithFilters(userId: UUID, filters: ConversationListFilters): Flux<ConversationEntity>

    fun countDailyNewConversations(userId: UUID, from: Instant, to: Instant): Flux<DailyActivityCount>
}
