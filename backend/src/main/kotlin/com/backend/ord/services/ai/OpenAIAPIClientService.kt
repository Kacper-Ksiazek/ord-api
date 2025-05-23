package com.backend.ord.services.ai

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.enums.GameType

interface OpenAIAPIClientService {
    fun <T> makeGameRequest(
        clazz: Class<T>,

        prompt: String,

        gameType: GameType,
        language: LanguageName,
        difficulty: GameDifficulty,
        consumptionType: GamesGPTTokensConsumptionType,

        user: UserEntity,

        parseResponseBody: (responseBody: T) -> T = { it },
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
    ): T
}