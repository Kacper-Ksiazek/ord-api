package com.ord.features.game.model.finished_game

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameGrade
import com.ord.features.game.model.ongoing_game.enums.GameResult
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("finished_games")
data class FinishedGameEntity(
    @Id
    override val id: UUID? = null,

    val score: Int,
    val accuracy: Float,
    val duration: String,

    val language: LanguageName,

    val type: GameType,
    val result: GameResult,
    val difficulty: GameDifficulty,
    val grade: GameGrade = GameGrade.NA,

    override val userId: UUID,

    val createdAt: Instant = Instant.now(),
) : IdentifiableUserResource

