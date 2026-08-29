package com.ord.testing_utils.api.clients

import com.ord.core.word.api.capture.requests.dto.PublicWordsBulkCaptureRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class PublicWordCaptureAPIClient(
    webClient: WebTestClient,
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/public/words"

    fun publicBulkCreate(
        body: PublicWordsBulkCaptureRequest,
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<Void?> {
        return post(
            url = "$baseUrl/bulk-create",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<Void>() {},
        )
    }
}
