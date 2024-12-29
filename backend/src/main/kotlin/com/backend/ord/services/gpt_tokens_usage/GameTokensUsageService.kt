package com.backend.ord.services.gpt_tokens_usage

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.services.gpt_tokens_usage.bases.TokensUsageServiceBase

interface GameTokensUsageService : TokensUsageServiceBase<GameTokensUsage, GamesGPTTokensConsumptionType> {
    fun save(
        user: User,

        gameType: GameType,
        leadingLanguage: LanguageName,
        gameDifficulty: GameDifficulty,
        instructionLanguage: LanguageName,
        consumptionType: GamesGPTTokensConsumptionType,

        inputTokens: Int,
        outputTokens: Int,
    ): GameTokensUsage

    fun assignGameToMultipleLogs(
        gptTokensUsageLogs: Set<GameTokensUsage>,
        gameToAssign: Game,
    ): List<GameTokensUsage>
}