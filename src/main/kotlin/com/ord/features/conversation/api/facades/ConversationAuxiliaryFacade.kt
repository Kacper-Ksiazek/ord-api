package com.ord.features.conversation.api.facades

import com.ord.features.conversation.api.facades.helpers.ai_responses.GeneratedAIInterlocutorData
import com.ord.features.conversation.api.requests.GenerateAIInterlocutorDataRequest
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface ConversationAuxiliaryFacade {
    fun suggestTopics(
        userId: UUID,
        body: SuggestConversationTopicRequest
    ): Flux<String>


    fun generateAIInterlocutorData(
        userId: UUID,
        body: GenerateAIInterlocutorDataRequest
    ): Mono<GeneratedAIInterlocutorData>
}