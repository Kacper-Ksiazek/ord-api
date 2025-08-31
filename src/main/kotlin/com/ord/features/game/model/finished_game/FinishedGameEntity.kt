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
    override var id: UUID? = null,

    @Column("duration")
    val duration: String,

    @Column("score")
    val score: Int,

    @Column("accuracy")
    val accuracy: Float,

    @Column("type")
    val type: GameType,

    @Column("language")
    val language: LanguageName,

    @Column("difficulty")
    val difficulty: GameDifficulty,

    @Column("result")
    val result: GameResult,

    @Column("grade")
    val grade: GameGrade = GameGrade.NA,

    @Column("user_id")
    override val userId: UUID,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Transient
    override val user: UserEntity? = null
) : IdentifiableUserResource

