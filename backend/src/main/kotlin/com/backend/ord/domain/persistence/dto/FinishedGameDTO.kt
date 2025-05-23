package com.backend.ord.domain.persistence.dto

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.features.game.model.enums.GameDifficulty
import com.backend.ord.features.game.model.enums.GameGrade
import com.backend.ord.features.game.model.enums.GameResult
import com.backend.ord.features.game.model.enums.GameType
import java.time.Instant
import java.util.*

data class FinishedGameDTO(
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