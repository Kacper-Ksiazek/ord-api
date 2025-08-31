package com.ord.features.game.model.ongoing_game

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("ongoing_games")
data class OngoingGameEntity(
    @Id
    override var id: UUID? = null,

    @Column("proper_answers")
    var properAnswers: String, // JSONB

    @Column("type")
    var type: GameType,

    @Column("language")
    var language: LanguageName,

    @Column("difficulty")
    var difficulty: GameDifficulty,

    @Column("user_id")
    override var userId: UUID,

    @Column("created_at")
    var createdAt: Instant = Instant.now(),

    @Transient
    override var user: UserEntity? = null
) : IdentifiableUserResource

