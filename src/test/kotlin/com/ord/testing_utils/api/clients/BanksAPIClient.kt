package com.ord.testing_utils.api.clients

import com.ord.features.bank.api.responses.BankListItem
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class BanksAPIClient(
    webClient: WebTestClient,
) : APITestClient(webClient) {
    private val baseUrl = "/api/v1/banks"

    fun listBanks(
        user: MockedAuthenticatedUser? = null,
    ): APIClientResponse<List<BankListItem>?> =
        get(
            url = baseUrl,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<List<BankListItem>>() {},
        )
}
