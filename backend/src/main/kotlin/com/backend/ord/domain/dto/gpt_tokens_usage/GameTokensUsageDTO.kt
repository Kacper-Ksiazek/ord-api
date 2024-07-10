package com.backend.ord.domain.dto.gpt_tokens_usage

import com.backend.ord.domain.dto.GameDTO
import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.enums.TokensUsage.GamesGPTTokensConsumptionType
import java.time.Instant
import java.util.*

data class GameTokensUsageDTO(
    val id: UUID = UUID.randomUUID(),

    var numberOfTokens: Int,
    var consumptionType: GamesGPTTokensConsumptionType,

    val user: UserDTO,
    val games: GameDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
