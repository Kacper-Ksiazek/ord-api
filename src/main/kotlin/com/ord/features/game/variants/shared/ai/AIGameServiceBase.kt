package com.ord.features.game.variants.shared.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.service.GameTokensUsageService
import org.springframework.beans.factory.annotation.Autowired

abstract class AIGameServiceBase {
    @Autowired
    protected lateinit var gameTokensUsageService: GameTokensUsageService

    @Autowired
    protected lateinit var openAIAPIClientService: OpenAIAPIClientService

    fun <T> makeGameAIRequest(
        aiResponseTypeReference: TypeReference<T>,
        prompt: String,

        gameType: GameType,
        language: LanguageName,
        difficulty: GameDifficulty,
        consumptionType: GamesGPTTokensConsumptionType,

        user: UserEntity,

        parseResponseBody: (T) -> T = { it },
        validateResponseBody: (T?) -> Boolean
    ): T {
        return openAIAPIClientService.makeRequest(
            prompt = prompt,
            validateResponseBody = validateResponseBody,
            parseResponseBody = parseResponseBody,
            aiResponseType = aiResponseTypeReference,
            saveLog = {
                gameTokensUsageService.save(
                    user = user,

                    gameDifficulty = difficulty,
                    language = language,

                    gameType = gameType,
                    consumptionType = consumptionType,

                    inputTokens = it.usage.input_tokens,
                    outputTokens = it.usage.output_tokens
                )
            },
        )
    }
}