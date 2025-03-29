package com.backend.ord.domain.persistence.dto.gpt_tokens_usage

import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class GameTokensUsageDTO(
    val id: UUID = UUID.randomUUID(),

    var gameType: GameType,
    val translatedFrom: LanguageName,
    var gameDifficulty: GameDifficulty,
    val instructionLanguage: LanguageName,
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
