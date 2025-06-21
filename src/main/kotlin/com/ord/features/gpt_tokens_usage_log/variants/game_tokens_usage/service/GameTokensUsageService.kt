package com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.service

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.GameTokensUsageEntity
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.features.gpt_tokens_usage_log.variants.shared.service.TokensUsageServiceBase

interface GameTokensUsageService : TokensUsageServiceBase<GameTokensUsageEntity, GamesGPTTokensConsumptionType> {
    fun save(
        user: UserEntity,

        gameType: GameType,
        language: LanguageName,
        gameDifficulty: GameDifficulty,
        consumptionType: GamesGPTTokensConsumptionType,

        inputTokens: Int,
        outputTokens: Int,
    ): GameTokensUsageEntity
}