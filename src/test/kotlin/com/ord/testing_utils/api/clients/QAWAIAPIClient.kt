package com.ord.testing_utils.api.clients

import com.ord.features.quickly_added_words.api.requests.QAWFillGapsRequest
import com.ord.features.quickly_added_words.api.responses.QAWFillGapsResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class QAWAIAPIClient(
    webClient: WebTestClient,
) : APITestClient(webClient) {
    private val baseUrl = "/api/v1/quickly-added-words/ai"

    fun fillGaps(
        body: QAWFillGapsRequest,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<QAWFillGapsResponse?> {
        return post(
            url = "$baseUrl/fill-gaps",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<QAWFillGapsResponse>() {},
        )
    }
}
