package com.ord.features.game.model.ongoing_game

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.shared.models.IdentifiableUserResource
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("ongoing_games")
data class OngoingGameEntity(
    @Id
    override val id: UUID? = null,

    val properAnswers: Json, // JSONB

    val language: LanguageName,

    val type: GameType,
    val difficulty: GameDifficulty,

    override val userId: UUID,

    val createdAt: Instant = Instant.now(),
) : IdentifiableUserResource

