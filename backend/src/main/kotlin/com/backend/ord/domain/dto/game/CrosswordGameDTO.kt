package com.backend.ord.domain.dto.game

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.GameStatus
import com.backend.ord.enums.game.GameType
import java.time.Instant
import java.util.*

data class CrosswordGameDTO(
    override val id: UUID = UUID.randomUUID(),
    override var finalScore: Int = 0,
    override var accuracyRate: Int = 0,
    override var acquiredPoints: Int = 0,
    override var duration: String = "00:00:00",

    override var instruction: CrosswordInstruction,

    override var difficulty: GameDifficulty,
    override var type: GameType,
    override var status: GameStatus = GameStatus.IN_PROGRESS,

    override val user: UserDTO,

    override val createdAt: Instant = Instant.now(),
    override var updatedAt: Instant = Instant.now()
) : GameDTOBase<CrosswordInstruction>(
    id = id,
    finalScore = finalScore,
    accuracyRate = accuracyRate,
    acquiredPoints = acquiredPoints,
    difficulty = difficulty,
    instruction = instruction,
    duration = duration,
    type = type,
    status = status,
    user = user,
    createdAt = createdAt,
    updatedAt = updatedAt
)

