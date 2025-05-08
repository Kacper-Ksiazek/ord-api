package com.backend.ord.services.ai

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType

interface OpenAIAPIClientService {
    fun <T> makeGameRequest(
        clazz: Class<T>,

        prompt: String,

        gameType: GameType,
        language: LanguageName,
        difficulty: GameDifficulty,
        consumptionType: GamesGPTTokensConsumptionType,

        user: User,

        parseResponseBody: (responseBody: T) -> T = { it },
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
    ): T
}