package com.ord.features.conversation.api.facades

import com.ord.features.conversation.models.conversation_activity.ConversationActivityOverviewDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface ConversationActivityFacade {
    fun getActivityOverview(userId: UUID): Mono<ResponseEntity<ConversationActivityOverviewDTO>>
}
