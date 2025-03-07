package com.backend.ord.services

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import org.springframework.stereotype.Service

@Service
interface UserActivityLogService {
    fun makeUserActivityLog(
        user: User,
        type: UserActivityType,
        language: LanguageName,
        difficulty: GameDifficulty
    ): Boolean
}