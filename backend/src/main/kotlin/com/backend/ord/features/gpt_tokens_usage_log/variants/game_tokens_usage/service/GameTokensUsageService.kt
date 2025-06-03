package com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.service

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.enums.GameType
import com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.GameTokensUsage
import com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.service.TokensUsageServiceBase

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