package com.ord.testing_utils.api.clients

import com.ord.features.game.variants.words_typing.dto.api_requests.FinishWordsTypingGameRequest
import com.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class WordsTypingGameAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/games/words-typing"

    fun startGame(
        body: StartGameRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<StartedWordsTypingGameResponse?> {
        return post(
            url = "$baseUrl/start",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<StartedWordsTypingGameResponse>() {}
        )
    }

    fun finishGame(
        body: FinishWordsTypingGameRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<FinishedWordsTypingGameResponse?> {
        return post(
            url = "$baseUrl/finish",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<FinishedWordsTypingGameResponse>() {}
        )
    }
}