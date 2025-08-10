package com.ord.features.conversation.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import reactor.core.publisher.Flux

interface ConversationTopicFacade {
    fun suggestTopics(
        user: UserEntity,
        body: SuggestConversationTopicRequest
    ): Flux<String>
}