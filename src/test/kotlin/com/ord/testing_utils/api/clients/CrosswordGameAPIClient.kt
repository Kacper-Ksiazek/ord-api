package com.ord.testing_utils.api.clients

import com.ord.features.game.variants.crossword.dto.api_requests.FinishCrosswordGameRequest
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class CrosswordGameAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/games/crossword"

    fun startGame(
        body: StartGameRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<StartedCrosswordGameResponse?> {
        return post(
            url = "$baseUrl/start",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<StartedCrosswordGameResponse>() {}
        )
    }

    fun finishGame(
        body: FinishCrosswordGameRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<FinishedCrosswordGameResponse?> {
        return post(
            url = "$baseUrl/finish",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<FinishedCrosswordGameResponse>() {}
        )
    }
}