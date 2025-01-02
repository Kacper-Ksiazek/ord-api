package com.backend.ord.domain.persistence.dto

import com.backend.ord.domain.persistence.dto.game.GameDTOBase
import java.time.Instant
import java.util.*

class UserProgressDTO(
    val id: UUID = UUID.randomUUID(),

    var pointsObtained: Int = 0,

    var user: UserDTO,
    var game: GameDTOBase<*, *>,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
