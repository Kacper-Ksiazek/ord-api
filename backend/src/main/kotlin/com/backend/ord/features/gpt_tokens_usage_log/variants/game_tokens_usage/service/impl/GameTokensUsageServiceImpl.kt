package com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.service.impl

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.enums.GameType
import com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.GameTokensUsageEntity
import com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.service.GameTokensUsageService
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.repository.TokensUsageRepository
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.service.impl.TokensUsageServiceBaseImpl
import org.springframework.stereotype.Service

@Service
class GameTokensUsageServiceImpl(
    override val repository: TokensUsageRepository<GameTokensUsageEntity, GamesGPTTokensConsumptionType>,
    override val openAIProperties: OpenAIProperties
) : GameTokensUsageService, TokensUsageServiceBaseImpl<GameTokensUsageEntity, GamesGPTTokensConsumptionType>(
    repository = repository,
    openAIProperties = openAIProperties
) {
    override fun save(
        user: UserEntity,
        gameType: GameType,
        language: LanguageName,
        gameDifficulty: GameDifficulty,
        consumptionType: GamesGPTTokensConsumptionType,
        inputTokens: Int,
        outputTokens: Int
    ): GameTokensUsageEntity {
        return repository.save(
            GameTokensUsageEntity(
                user = user,

                language = language,
                gameType = gameType,
                gameDifficulty = gameDifficulty,
                consumptionType = consumptionType,

                inputTokens = inputTokens,
                outputTokens = outputTokens,
                priceForMlnInputTokens = openAIProperties.pricePerMlnInputTokens,
                priceForMlnOutputTokens = openAIProperties.pricePerMlnOutputTokens,
                cost = computeCost(inputTokens, outputTokens)

            )
        )
    }
}