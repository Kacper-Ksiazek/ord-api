package com.ord.testing_utils.api.clients

import com.ord.core.word.api.ai.requests.dto.WordFillGapsRequest
import com.ord.core.word.api.ai.responses.dto.WordFillGapsResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class WordFillGapsAIAPIClient(
    webClient: WebTestClient,
) : APITestClient(webClient) {
    private val baseUrl = "/api/v1/words/ai"

    fun fillGaps(
        body: WordFillGapsRequest,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<WordFillGapsResponse?> {
        return post(
            url = "$baseUrl/fill-gaps",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordFillGapsResponse>() {},
        )
    }
}
