package com.ord.testing_utils.api.clients

import com.ord.core.user.api.responses.MeResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class UsersAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/users"

    fun me(
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<MeResponse?> {
        return get(
            url = "$baseUrl/me",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<MeResponse>() {}
        )
    }
}
