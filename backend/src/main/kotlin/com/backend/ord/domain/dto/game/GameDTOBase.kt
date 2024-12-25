package com.backend.ord.domain.dto.game

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.GameStatus
import com.backend.ord.enums.game.GameType
import java.time.Instant
import java.util.*

abstract class GameDTOBase<Instructions>(
    open val id: UUID = UUID.randomUUID(),

    open var finalScore: Int = 0,
    open var accuracyRate: Int = 0,
    open var acquiredPoints: Int = 0,
    open var difficulty: GameDifficulty,

    open var instruction: Instructions,

    open var duration: String = "00:00:00",

    open var type: GameType,
    open var status: GameStatus = GameStatus.IN_PROGRESS,

    open val user: UserDTO,

    open val createdAt: Instant = Instant.now(),
    open var updatedAt: Instant = Instant.now()
)


