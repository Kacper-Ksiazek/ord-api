package com.ord.features.ai_explainer.api.facades

import com.ord.core.user.model.UserDTO
import com.ord.features.ai_explainer.api.requests.ExplainPhraseAutocompleteRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseEnhanceRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseSuggestActionsRequest
import com.ord.features.ai_explainer.api.responses.ExplainPhraseAutocompleteResponse
import com.ord.features.ai_explainer.api.responses.ExplainPhraseEnhanceResponse
import com.ord.features.ai_explainer.api.responses.ExplainPhraseSuggestActionsResponse
import com.ord.features.ai_explainer.models.explanation.AIExplanationDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

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

    fun enhancePrompt(
        body: ExplainPhraseEnhanceRequest,
        user: UserDTO,
    ): Mono<ExplainPhraseEnhanceResponse>

    fun suggestActions(
        body: ExplainPhraseSuggestActionsRequest,
        user: UserDTO,
    ): Mono<ExplainPhraseSuggestActionsResponse>

    fun getExplanation(
        id: UUID,
        user: UserDTO,
    ): Mono<AIExplanationDTO>

    fun getExplanations(
        user: UserDTO,
    ): Mono<ResponseEntity<List<AIExplanationDTO>>>

    fun deleteExplanation(
        id: UUID,
        user: UserDTO,
    ): Mono<ResponseEntity<Unit>>
}
