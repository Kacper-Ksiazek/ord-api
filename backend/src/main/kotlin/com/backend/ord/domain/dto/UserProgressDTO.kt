package com.backend.ord.domain.dto

import java.time.Instant
import java.util.*

class UserProgressDTO(
    val id: UUID = UUID.randomUUID(),

    var pointsObtained: Int = 0,

    var user: UserDTO,
    var game: GameDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
