package com.ord.testing_utils.api.clients

import com.ord.features.ai_explainer.api.requests.ExplainPhraseAutocompleteRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseEnhanceRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseSuggestActionsRequest
import com.ord.features.ai_explainer.api.responses.ExplainPhraseAutocompleteResponse
import com.ord.features.ai_explainer.api.responses.ExplainPhraseEnhanceResponse
import com.ord.features.ai_explainer.api.responses.ExplainPhraseSuggestActionsResponse
import com.ord.features.ai_explainer.models.explanation.AIExplanationDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import java.util.UUID

class AIExplainerAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/ai-explainer"

    fun explainPhrase(
        body: ExplainPhraseRequest,
        user: MockedAuthenticatedUser? = null
    ): ExplainPhraseStreamResponse {
        val requestSpec = webClient
            .post()
            .uri("$baseUrl/explain-phrase")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .apply {
                if (user != null) {
                    this.cookie(user.authCookie.name, user.authCookie.value)
                }
            }
            .bodyValue(body)

        val result = requestSpec
            .exchange()
            .returnResult(String::class.java)

        val status = HttpStatus.valueOf(result.status.value())

        if (status != HttpStatus.OK) {
            return ExplainPhraseStreamResponse(
                explanation = "",
                status = status
            )
        }

        // Collect all streamed text chunks
        val explanation = result.responseBody
            .filter { it.isNotBlank() }
            .collectList()
            .block(Duration.ofSeconds(180))
            ?.joinToString("")
            ?: ""

        return ExplainPhraseStreamResponse(
            explanation = explanation,
            status = status
        )
    }

    fun followUpExplainPhrase(
        body: ExplainPhraseFollowUpRequest,
        user: MockedAuthenticatedUser? = null
    ): ExplainPhraseStreamResponse {
        val requestSpec = webClient
            .post()
            .uri("$baseUrl/explain-phrase/follow-up")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .apply {
                if (user != null) {
                    this.cookie(user.authCookie.name, user.authCookie.value)
                }
            }
            .bodyValue(body)

        val result = requestSpec
            .exchange()
            .returnResult(String::class.java)

        val status = HttpStatus.valueOf(result.status.value())

        if (status != HttpStatus.OK) {
            return ExplainPhraseStreamResponse(
                explanation = "",
                status = status
            )
        }

        val explanation = result.responseBody
            .filter { it.isNotBlank() }
            .collectList()
            .block(Duration.ofSeconds(180))
            ?.joinToString("")
            ?: ""

        return ExplainPhraseStreamResponse(
            explanation = explanation,
            status = status
        )
    }

    fun autocompletePhrase(
        body: ExplainPhraseAutocompleteRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<ExplainPhraseAutocompleteResponse?> = post(
        url = "$baseUrl/autocomplete",
        body = body,
        user = user,
        responseBodyType = object : ParameterizedTypeReference<ExplainPhraseAutocompleteResponse>() {}
    )

    fun enhancePrompt(
        body: ExplainPhraseEnhanceRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<ExplainPhraseEnhanceResponse?> = post(
        url = "$baseUrl/explain-phrase/enhance",
        body = body,
        user = user,
        responseBodyType = object : ParameterizedTypeReference<ExplainPhraseEnhanceResponse>() {}
    )

    fun suggestActions(
        body: ExplainPhraseSuggestActionsRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<ExplainPhraseSuggestActionsResponse?> = post(
        url = "$baseUrl/explain-phrase/suggest-actions",
        body = body,
        user = user,
        responseBodyType = object : ParameterizedTypeReference<ExplainPhraseSuggestActionsResponse>() {}
    )

    fun getExplanations(
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<List<AIExplanationDTO>?> = get(
        url = "$baseUrl/explanations",
        user = user,
        responseBodyType = object : ParameterizedTypeReference<List<AIExplanationDTO>>() {}
    )

    fun getExplanation(
        id: UUID,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<AIExplanationDTO?> = get(
        url = "$baseUrl/explanations/$id",
        user = user,
        responseBodyType = object : ParameterizedTypeReference<AIExplanationDTO>() {}
    )

    fun deleteExplanation(
        id: UUID,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> = delete<Unit>(
        url = "$baseUrl/explanations/$id",
        user = user
    )

    data class ExplainPhraseStreamResponse(
        val explanation: String,
        val status: HttpStatus
    )
}