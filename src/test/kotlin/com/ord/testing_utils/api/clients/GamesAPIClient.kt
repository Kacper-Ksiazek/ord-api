package com.ord.testing_utils.api.clients

import com.ord.features.game.variants.shared.dto.api_requests.CancelGameRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class GamesAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/games"

    fun cancelGame(
        gameId: UUID,
        body: CancelGameRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return delete<Unit>(
            url = "$baseUrl/cancel/$gameId",
            user = user
        )
    }
}