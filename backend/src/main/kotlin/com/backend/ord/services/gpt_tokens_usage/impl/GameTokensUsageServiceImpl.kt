package com.backend.ord.services.gpt_tokens_usage.impl

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.repositories.gpt_tokens_usage.bases.GPTTokensUsageRepository
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.backend.ord.services.gpt_tokens_usage.bases.impl.TokensUsageServiceBaseImpl
import org.springframework.stereotype.Service

@Service
class GameTokensUsageServiceImpl(
    override val repository: GPTTokensUsageRepository<GameTokensUsage, GamesGPTTokensConsumptionType>,
    override val openAIProperties: OpenAIProperties
) : GameTokensUsageService, TokensUsageServiceBaseImpl<GameTokensUsage, GamesGPTTokensConsumptionType>(
    repository = repository,
    openAIProperties = openAIProperties
) {
    override fun save(
        user: User,
        gameType: GameType,
        leadingLanguage: LanguageName,
        gameDifficulty: GameDifficulty,
        instructionLanguage: LanguageName,
        consumptionType: GamesGPTTokensConsumptionType,
        inputTokens: Int,
        outputTokens: Int
    ): GameTokensUsage {
        return repository.save(
            GameTokensUsage(
                user = user,

                gameType = gameType,
                gameDifficulty = gameDifficulty,
                translatedFrom = leadingLanguage,
                consumptionType = consumptionType,
                instructionLanguage = instructionLanguage,

                inputTokens = inputTokens,
                outputTokens = outputTokens,
                priceForMlnInputTokens = openAIProperties.pricePerMlnInputTokens,
                priceForMlnOutputTokens = openAIProperties.pricePerMlnOutputTokens,
                cost = computeCost(inputTokens, outputTokens)

            )
        )
    }

    override fun assignGameToMultipleLogs(
        gptTokensUsageLogs: Set<GameTokensUsage>,
        gameToAssign: Game
    ): List<GameTokensUsage> {
        return repository.saveAll(
            gptTokensUsageLogs.map {
                it.game = gameToAssign
                it
            }
        )
    }
}