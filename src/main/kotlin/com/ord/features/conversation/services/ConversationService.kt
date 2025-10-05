package com.ord.features.conversation.services

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.enums.ConversationType
import com.ord.shared.services.UserResourceService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ConversationService : UserResourceService<ConversationEntity> {
    fun findRecentTopics(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int = 10
    ): Flux<String>


    fun findByIdOrFailWithMessages(
        id: UUID,
        userId: UUID
    ): Mono<ConversationDTO>
}
