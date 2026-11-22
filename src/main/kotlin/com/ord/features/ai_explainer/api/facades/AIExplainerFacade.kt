package com.ord.features.ai_explainer.api.facades

import com.ord.core.user.model.UserDTO
import com.ord.features.ai_explainer.api.requests.ExplainPhraseAutocompleteRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.features.ai_explainer.api.responses.ExplainPhraseAutocompleteResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AIExplainerFacade {
    fun explainPhrase(
        body: ExplainPhraseRequest,
        user: UserDTO,
    ): Flux<String>

    fun followUpExplainPhrase(
        body: ExplainPhraseFollowUpRequest,
        user: UserDTO,
    ): Flux<String>

    fun autocompletePhrase(
        body: ExplainPhraseAutocompleteRequest,
        user: UserDTO,
    ): Mono<ExplainPhraseAutocompleteResponse>
}
