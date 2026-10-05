package com.ord.testing_utils.api.clients

import com.ord.features.home.model.HomeActivityPerDay
import com.ord.features.home.model.HomeResponse
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class HomeAPIClient(
    webClient: WebTestClient,
) : APITestClient(webClient) {
    fun getHome(
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<HomeResponse?> =
        get(
            url = "/api/v1/home",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<HomeResponse>() {},
        )

    fun getActivity(
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<HomeActivityPerDay?> =
        get(
            url = "/api/v1/home/activity",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<HomeActivityPerDay>() {},
        )
}
