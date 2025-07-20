package com.ord.features.game.model.finished_game

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameGrade
import com.ord.features.game.model.ongoing_game.enums.GameResult
import com.ord.features.game.model.ongoing_game.enums.GameType
import java.time.Instant
import java.util.*

data class FinishedGameDTO(
    val id: UUID = UUID.randomUUID(),

    val duration: String,
    val score: Int,
    val accuracy: Float,

    val type: GameType,
    val grade: GameGrade,
    val result: GameResult,
    val language: LanguageName,
    val difficulty: GameDifficulty,

    var user: UserDTO,
    var userId: UUID = user.id,

    var createdAt: Instant = Instant.now()
)