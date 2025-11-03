package com.ord.testing_utils.api.clients

import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

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

    data class ExplainPhraseStreamResponse(
        val explanation: String,
        val status: HttpStatus
    )
}