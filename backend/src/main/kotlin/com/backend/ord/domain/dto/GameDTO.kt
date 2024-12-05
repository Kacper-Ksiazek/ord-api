package com.backend.ord.domain.dto

import com.backend.ord.enums.game.GameStatus
import com.backend.ord.enums.game.GameType
import java.time.Instant
import java.util.*

data class GameDTO(
    val id: UUID = UUID.randomUUID(),

    var finalScore: Int = 0,
    var accuracyRate: Int = 0,
    var acquiredPoints: Int = 0,

    var duration: String = "00:00:00",

    var type: GameType,
    var status: GameStatus = GameStatus.IN_PROGRESS,

    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
)
