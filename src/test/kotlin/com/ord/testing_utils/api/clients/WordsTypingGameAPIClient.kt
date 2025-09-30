package com.ord.testing_utils.api.clients

import com.ord.features.game.variants.words_typing.dto.api_requests.FinishWordsTypingGameRequest
import com.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import com.ord.testing_utils.api.clients.bases.GameAPIClient
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class WordsTypingGameAPIClient(
    webClient: WebTestClient
) : GameAPIClient<
        StartedWordsTypingGameResponse,
        FinishWordsTypingGameRequest,
        FinishedWordsTypingGameResponse
        >(
    webClient,
    gameSlugName = "words-typing",
    finishedGameTypeReference = object : ParameterizedTypeReference<FinishedWordsTypingGameResponse>() {},
    startedGameTypeReference = object : ParameterizedTypeReference<StartedWordsTypingGameResponse>() {}
)