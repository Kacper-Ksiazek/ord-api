package com.ord.testing_utils.api.clients

import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class SentencesWritingGameAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/games/sentences-writing"

    fun startGame(
        body: StartGameRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<StartedSentencesWritingGameResponse?> {
        return post(
            url = "$baseUrl/start",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<StartedSentencesWritingGameResponse>() {}
        )
    }

    fun finishGame(
        body: FinishSentencesWritingGameRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<FinishedSentencesWritingGameResponse?> {
        return post(
            url = "$baseUrl/finish",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<FinishedSentencesWritingGameResponse>() {}
        )
    }
}