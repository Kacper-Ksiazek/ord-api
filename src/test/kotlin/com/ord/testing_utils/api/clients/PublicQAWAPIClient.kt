package com.ord.testing_utils.api.clients

import com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class PublicQAWAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/public/quickly-added-words"

    fun publicBulkCreate(
        body: PublicQAWBulkCreateRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<List<QuicklyAddedWordEntity>?> {
        return post(
            url = "$baseUrl/bulk-create",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<List<QuicklyAddedWordEntity>>() {}
        )
    }
}