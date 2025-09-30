package com.ord.testing_utils.api.clients.bases

import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_requests.UnsafeStartGameRequestData
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

abstract class GameAPIClient<
        TStartedGameResponseBody,
        TFinishGameRequestBody,
        TFinishedGameResponseBody
        >(
    webClient: WebTestClient,
    private val gameSlugName: String,
    private val startedGameTypeReference: ParameterizedTypeReference<TStartedGameResponseBody>,
    private val finishedGameTypeReference: ParameterizedTypeReference<TFinishedGameResponseBody>
) : APITestClient(webClient) {

    val baseUrl: String = "/api/v1/games/$gameSlugName"

    fun startGame(
        body: UnsafeStartGameRequestData,
        user: MockedAuthenticatedUser? = null
    ) = post(
        url = "$baseUrl/start",
        body = body,
        user = user,
        responseBodyType = startedGameTypeReference
    )


    fun startGame(
        body: StartGameRequest,
        user: MockedAuthenticatedUser? = null
    ) = startGame(
        user = user,
        body = UnsafeStartGameRequestData(
            language = body.language,
            difficulty = body.difficulty,
        ),
    )


    fun finishGame(
        body: TFinishGameRequestBody,
        user: MockedAuthenticatedUser? = null
    ) = post(
        url = "$baseUrl/finish",
        body = body,
        user = user,
        responseBodyType = finishedGameTypeReference
    )
}