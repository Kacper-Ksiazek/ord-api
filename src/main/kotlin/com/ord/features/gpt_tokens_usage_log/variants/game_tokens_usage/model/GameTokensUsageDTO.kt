package com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class GameTokensUsageDTO(
    val id: UUID = UUID.randomUUID(),

    var gameType: GameType,
    val language: LanguageName,
    var gameDifficulty: GameDifficulty,
    var consumptionType: GamesGPTTokensConsumptionType,

    val cost: BigDecimal,
    val inputTokens: Int,
    val outputTokens: Int,
    val priceForMlnInputTokens: BigDecimal,
    val priceForMlnOutputTokens: BigDecimal,

    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)