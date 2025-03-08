package com.backend.ord.services

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import java.util.*

interface UserActivityLogService {
    /**
     * Logs new user activity and saves it to the database
     * Returns true if the log was successfully saved and false if it wasn't
     */
    fun log(
        user: User,
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
}