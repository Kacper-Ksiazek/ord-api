package com.backend.ord.services.impl.ai

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.services.ai.OpenAIAPIClientService
import org.springframework.stereotype.Service

@Service
class OpenAIAPIClientServiceImpl : OpenAIAPIClientService {
    override fun <OpenAIResponse> makeGameRequest(
        user: User,
        prompt: String,
        gameType: GameType,
        consumptionType: GamesGPTTokensConsumptionType,

        retryRequestCondition: (parsedResponseBody: OpenAIResponse?) -> Boolean,

        parseResponseBody: ((responseBody: OpenAIResponse) -> OpenAIResponse)? = null,

        ) {
        TODO("Not yet implemented")
    }

    private fun <OpenAIResponse> makeRequest(
        //
    ) {
        TODO("Not yet implemented")
    }
}