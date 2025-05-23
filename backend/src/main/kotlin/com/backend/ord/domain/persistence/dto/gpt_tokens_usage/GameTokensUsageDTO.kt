package com.backend.ord.domain.persistence.dto.gpt_tokens_usage

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.features.game.model.enums.GameDifficulty
import com.backend.ord.features.game.model.enums.GameType
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
