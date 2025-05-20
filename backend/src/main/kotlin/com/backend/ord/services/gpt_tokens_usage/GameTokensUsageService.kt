package com.backend.ord.services.gpt_tokens_usage

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.features.ongoing_game.model.enums.GameDifficulty
import com.backend.ord.features.ongoing_game.model.enums.GameType
import com.backend.ord.services.gpt_tokens_usage.bases.TokensUsageServiceBase

interface GameTokensUsageService : TokensUsageServiceBase<GameTokensUsage, GamesGPTTokensConsumptionType> {
    fun save(
        user: UserEntity,

        gameType: GameType,
        language: LanguageName,
        gameDifficulty: GameDifficulty,
        consumptionType: GamesGPTTokensConsumptionType,

        inputTokens: Int,
        outputTokens: Int,
    ): GameTokensUsage
}