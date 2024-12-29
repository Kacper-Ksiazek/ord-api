package com.backend.ord.domain.persistance.dto.gpt_tokens_usage

import com.backend.ord.domain.persistance.dto.UserDTO
import com.backend.ord.domain.persistance.dto.game.GameDTOBase
import com.backend.ord.enums.persistance.game.GameDifficulty
import com.backend.ord.enums.persistance.game.GameType
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.enums.persistance.tokens_usage.GamesGPTTokensConsumptionType
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
    val game: GameDTOBase<*>? = null,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
