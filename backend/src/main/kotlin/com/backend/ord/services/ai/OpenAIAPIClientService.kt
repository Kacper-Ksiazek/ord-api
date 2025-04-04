package com.backend.ord.services.ai

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType

interface OpenAIAPIClientService {
    fun <T> makeGameRequest(
        clazz: Class<T>,

        user: User,
        prompt: String,
        gameType: GameType,
        difficulty: GameDifficulty,
        leadingLanguage: LanguageName,
        instructionLanguage: LanguageName,
        consumptionType: GamesGPTTokensConsumptionType,

        retryRequestCondition: (parsedResponseBody: T?) -> Boolean,

        parseResponseBody: (responseBody: T) -> T = { it },

        ): T
}