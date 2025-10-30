package com.ord.features.ai_explainer.api.facades

import com.ord.core.user.model.UserDTO
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import reactor.core.publisher.Flux

interface AIExplainerFacade {
    fun explainPhrase(
        body: ExplainPhraseRequest,
        user: UserDTO,
    ): Flux<String>
}