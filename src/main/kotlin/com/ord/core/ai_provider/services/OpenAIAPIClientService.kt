package com.ord.core.ai_provider.services

import com.ord.core.ai_provider.dto.OpenAIResponse
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType

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

    fun <T> makeRequest(
        clazz: Class<T>,

        prompt: String,

        saveLog: (openAIResponse: OpenAIResponse) -> Unit,
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
        parseResponseBody: (responseBody: T) -> T = { it }
    ): T
}
