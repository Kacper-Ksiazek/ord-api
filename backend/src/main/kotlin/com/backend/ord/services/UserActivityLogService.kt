package com.backend.ord.services

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.persistence.entities.UserActivityLog
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import java.util.*

interface UserActivityLogService {
    /**
     * Logs new user activity and saves it to the database
     * Returns true if the log was successfully saved and false if it wasn't
     */
    fun log(
        user: UserEntity,
        type: UserActivityType,
        language: LanguageName,
        difficulty: GameDifficulty? = null
    ): Boolean

    fun log(
        userId: UUID,
        type: UserActivityType,
        language: LanguageName,
        difficulty: GameDifficulty? = null
    ): Boolean

    fun logMany(userActivityLogs: Set<UserActivityLog>): Set<UserActivityLog>
}