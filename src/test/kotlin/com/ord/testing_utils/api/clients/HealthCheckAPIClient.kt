package com.ord.testing_utils.api.clients

import com.ord.core.health.HealthCheckResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class HealthCheckAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    fun healthCheck(
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<HealthCheckResponse?> {
        return get(
            url = "/api/v1/health-check",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<HealthCheckResponse>() {}
        )
    }
}
