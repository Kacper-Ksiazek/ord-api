package com.backend.ord.services.gpt_tokens_usage

import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.services.gpt_tokens_usage.bases.TokensUsageServiceBase

interface GameTokensUsageService : TokensUsageServiceBase<GameTokensUsage, GamesGPTTokensConsumptionType> {
    // TODO: Ogarnac jak polaczyc relacja z gra, bo UUID nie jest znane w momencie generowania tego logu

    fun save(
        user: User,
        game: String,
        consumptionType: GamesGPTTokensConsumptionType,
        inputTokens: Int,
        outputTokens: Int,
    ): GameTokensUsage
}