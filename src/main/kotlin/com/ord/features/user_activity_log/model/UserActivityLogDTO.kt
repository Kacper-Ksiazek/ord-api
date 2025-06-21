package com.ord.features.user_activity_log.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.user_activity_log.model.enums.UserActivityType
import java.time.Instant
import java.util.*

class UserActivityLogDTO(
    val id: UUID = UUID.randomUUID(),

    val points: Int = 0,
    val type: UserActivityType,
    val language: LanguageName,
    val gameDifficulty: GameDifficulty? = null,

    var user: UserDTO,
    var userId: UUID = user.id,

    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
)