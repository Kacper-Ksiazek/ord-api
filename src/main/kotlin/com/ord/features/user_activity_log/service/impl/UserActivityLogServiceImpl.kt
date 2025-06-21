package com.ord.features.user_activity_log.service.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.UserRepository
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.features.user_activity_log.model.enums.UserActivityFrequency
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.ord.features.user_activity_log.service.UserActivityLogService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserActivityLogServiceImpl(
    val repository: UserActivityLogRepository,
    val userRepository: UserRepository
) : UserActivityLogService {
    private fun checkIfLogCanBeAdded(
        userId: UUID,
        type: UserActivityType,
        language: LanguageName
    ): Boolean {
        return when (type.frequency) {
            UserActivityFrequency.NON_PERIODIC -> true

            UserActivityFrequency.DAILY -> repository.countDailyLog(userId, type, language) == 0
            UserActivityFrequency.WEEKLY -> repository.countWeeklyLog(userId, type, language) == 0
            UserActivityFrequency.MONTHLY -> repository.countMonthlyLog(userId, type, language) == 0
        }
    }

    override fun log(
        user: UserEntity,
        type: UserActivityType,
        language: LanguageName,
        difficulty: GameDifficulty?
    ): Boolean {
        if (checkIfLogCanBeAdded(user.id, type, language)) {
            repository.save(
                UserActivityLogEntity(
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

    override fun log(
        userId: UUID,
        type: UserActivityType,
        language: LanguageName,
        difficulty: GameDifficulty?
    ): Boolean {
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundException("User with ID $userId not found")

        return log(
            user = user,
            type = type,
            language = language,
            difficulty = difficulty
        )
    }

    override fun logMany(userActivityLogEntities: Set<UserActivityLogEntity>): Set<UserActivityLogEntity> {
        return repository.saveAll(
            userActivityLogEntities.filter {
                checkIfLogCanBeAdded(
                    userId = it.user.id,
                    type = it.type,
                    language = it.language
                )
            }
        ).toSet()
    }
}