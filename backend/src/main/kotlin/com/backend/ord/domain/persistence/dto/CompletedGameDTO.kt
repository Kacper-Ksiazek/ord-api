package com.backend.ord.domain.persistence.dto

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.enums.persistence.game.GameResult
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import java.time.Instant
import java.util.*

data class CompletedGameDTO(
    val id: UUID = UUID.randomUUID(),

    val duration: String,
    val finalScore: Int,

    val type: GameType,
    val grade: GameGrade,
    val result: GameResult,
    val language: LanguageName,
    val difficulty: GameDifficulty,

    var user: UserDTO,
    var userId: UUID = user.id,

    var createdAt: Instant = Instant.now()
)