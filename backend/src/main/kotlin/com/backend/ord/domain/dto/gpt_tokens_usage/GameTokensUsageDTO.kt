package com.backend.ord.domain.dto.gpt_tokens_usage

import com.backend.ord.domain.dto.GameDTO
import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.GameType
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.tokens_usage.GamesGPTTokensConsumptionType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class GameTokensUsageDTO(
    val id: UUID = UUID.randomUUID(),

    var gameType: GameType,
    val leadingLanguage: LanguageName,
    var gameDifficulty: GameDifficulty,
    val instructionLanguage: LanguageName,
    var consumptionType: GamesGPTTokensConsumptionType,

    val cost: BigDecimal,
    val inputTokens: Int,
    val outputTokens: Int,
    val priceForMlnInputTokens: BigDecimal,
    val priceForMlnOutputTokens: BigDecimal,

    val user: UserDTO,
    val game: GameDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
