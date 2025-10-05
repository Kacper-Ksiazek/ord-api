package com.ord.features.conversation.api.facades

import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import reactor.core.publisher.Flux
import java.util.UUID

interface ConversationAuxiliaryFacade {
    fun suggestTopics(
        userId: UUID,
        body: SuggestConversationTopicRequest
    ): Flux<String>
}