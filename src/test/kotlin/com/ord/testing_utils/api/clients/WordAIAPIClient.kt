package com.ord.testing_utils.api.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.word.api.ai.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest
import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import com.ord.core.word.api.ai.responses.dto.VocabularySuggestion
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

class WordAIAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/words/ai"
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun generateManual(
        body: GenerateWordManualRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<AIGeneratedWordManual?> {
        return post(
            url = "$baseUrl/generate-manual",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<AIGeneratedWordManual>() {}
        )
    }

    fun suggestVocabulary(
        body: SuggestVocabularyRequest,
        user: MockedAuthenticatedUser? = null
    ): SuggestVocabularyStreamResponse {
        val requestSpec = webClient
            .post()
            .uri("$baseUrl/suggest-vocabulary")
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
            return SuggestVocabularyStreamResponse(
                suggestions = emptyList(),
                status = status
            )
        }

        // In test environment, all items are collected and returned at once
        val suggestions = result.responseBody
            .filter { it.isNotBlank() }
            .mapNotNull { json ->
                try {
                    objectMapper.readValue(json, VocabularySuggestion::class.java)
                } catch (e: Exception) {
                    null
                }
            }
            .collectList()
            .block(Duration.ofSeconds(180))
            ?: emptyList()

        return SuggestVocabularyStreamResponse(
            suggestions = suggestions.filterNotNull(),
            status = status
        )
    }

    data class SuggestVocabularyStreamResponse(
        val suggestions: List<VocabularySuggestion>,
        val status: HttpStatus
    )
}