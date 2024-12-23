package com.backend.ord.services.gpt_tokens_usage

import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.GameType
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.tokens_usage.GamesGPTTokensConsumptionType
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

    fun assignGameToMultiple(
        gptTokensUsageLogs: Set<GameTokensUsage>,
        gameToAssign: Game,
    ): List<GameTokensUsage>
}