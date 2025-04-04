package com.backend.ord.services.ai

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType

interface OpenAIAPIClientService {
    fun <OpenAIResponse> makeGameRequest(
        user: User,
        prompt: String,
        gameType: GameType,
        consumptionType: GamesGPTTokensConsumptionType,

        retryRequestCondition: (parsedResponseBody: OpenAIResponse?) -> Boolean,

        parseResponseBody: ((responseBody: OpenAIResponse) -> OpenAIResponse)? = null,
    )
}