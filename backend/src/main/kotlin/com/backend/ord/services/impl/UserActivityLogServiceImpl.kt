package com.backend.ord.services.impl

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.UserActivityLog
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.repositories.UserActivityLogRepository
import com.backend.ord.services.UserActivityLogService

class UserActivityLogServiceImpl(
    val userActivityLogRepository: UserActivityLogRepository
) : UserActivityLogService {
    override fun makeUserActivityLog(
        user: User,
        type: UserActivityType,
        language: LanguageName,
        difficulty: GameDifficulty
    ): Boolean {
        if (userActivityLogRepository.checkIfLogCanBeAdded(user.id, type, language)) {
            userActivityLogRepository.save(
                UserActivityLog(
                    user = user,
                    type = type,
                    language = language,
                    gameDifficulty = difficulty,
                    points = type.points
                )
            )
            return true
        }

        return false
    }
}
