package com.ord.testing_utils.api.clients.games

import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.clients.games.bases.GameAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class SentencesWritingGameAPIClient(
    webClient: WebTestClient
) : GameAPIClient<
        StartedSentencesWritingGameResponse,
        FinishSentencesWritingGameRequest,
        FinishedSentencesWritingGameResponse
        >(
    webClient,
    gameSlugName = "sentences-writing",
    finishedGameTypeReference = object : ParameterizedTypeReference<FinishedSentencesWritingGameResponse>() {},
    startedGameTypeReference = object : ParameterizedTypeReference<StartedSentencesWritingGameResponse>() {}
)