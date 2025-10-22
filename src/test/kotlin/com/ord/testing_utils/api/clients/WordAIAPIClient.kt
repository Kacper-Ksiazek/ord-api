package com.ord.testing_utils.api.clients

import com.ord.core.word.api.ai.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class WordAIAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/words/ai"

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
}