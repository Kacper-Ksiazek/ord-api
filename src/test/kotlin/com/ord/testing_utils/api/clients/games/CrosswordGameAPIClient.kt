package com.ord.testing_utils.api.clients.games

import com.ord.features.game.variants.crossword.dto.api_requests.FinishCrosswordGameRequest
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.clients.games.bases.GameAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class CrosswordGameAPIClient(
    webClient: WebTestClient
) : GameAPIClient<
        StartedCrosswordGameResponse,
        FinishCrosswordGameRequest,
        FinishedCrosswordGameResponse
        >(
    webClient,
    gameSlugName = "crossword",
    finishedGameTypeReference = object : ParameterizedTypeReference<FinishedCrosswordGameResponse>() {},
    startedGameTypeReference = object : ParameterizedTypeReference<StartedCrosswordGameResponse>() {}
)