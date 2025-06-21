package com.ord.features.user_activity_log.service

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.features.user_activity_log.model.enums.UserActivityType
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

    fun logMany(userActivityLogEntities: Set<UserActivityLogEntity>): Set<UserActivityLogEntity>
}