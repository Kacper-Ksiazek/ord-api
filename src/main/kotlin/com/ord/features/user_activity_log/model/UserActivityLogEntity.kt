package com.ord.features.user_activity_log.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("user_activity_logs")
data class UserActivityLogEntity(
    @Id
    override var id: UUID = UUID.randomUUID(),

    val type: UserActivityType,
    val language: LanguageName,
    val gameDifficulty: GameDifficulty? = null,
    val points: Int = type.points,

    override val userId: UUID,

    var createdAt: Instant = Instant.now(),

    @Transient override var user: UserEntity? = null,
) : IdentifiableUserResource