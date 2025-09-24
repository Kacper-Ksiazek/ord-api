package com.ord.features.user_activity_log.service.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepositoryReactive
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.features.user_activity_log.model.enums.UserActivityFrequency
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.ord.features.user_activity_log.service.UserActivityLogService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserActivityLogServiceImpl(
    val repository: UserActivityLogRepository,
    val userRepository: UserRepositoryReactive
) : UserActivityLogService {
    private fun checkIfLogCanBeAdded(
        userId: UUID,
        type: UserActivityType,
        language: LanguageName
    ): Mono<Boolean> {
        return when (type.frequency) {
            UserActivityFrequency.NON_PERIODIC -> Mono.just(true)

            UserActivityFrequency.DAILY -> repository.countDailyLog(userId, type, language)
                .map { count -> count == 0L }

            UserActivityFrequency.WEEKLY -> repository.countWeeklyLog(userId, type, language)
                .map { count -> count == 0L }

            UserActivityFrequency.MONTHLY -> repository.countMonthlyLog(userId, type, language)
                .map { count -> count == 0L }
        }
    }

    override fun log(
        userId: UUID,
        type: UserActivityType,
        language: LanguageName,
        difficulty: GameDifficulty?
    ): Mono<Boolean> {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(NotFoundException("User with ID $userId not found")))
            .flatMap { user ->
                checkIfLogCanBeAdded(
                    userId = userId,
                    type = type,
                    language = language
                )
                    .flatMap { canBeAdded ->
                        if (canBeAdded) {
                            repository.save(
                                UserActivityLogEntity(
                                    userId = userId,
                                    type = type,
                                    language = language,
                                    gameDifficulty = difficulty,
                                    user = user
                                )
                            ).map { true }
                        } else {
                            Mono.just(false)
                        }
                    }
            }
    }

    override fun logMany(userActivityLogEntities: Set<UserActivityLogEntity>): Flux<UserActivityLogEntity> {
        return Flux.fromIterable(userActivityLogEntities)
            .filterWhen { entity ->
                checkIfLogCanBeAdded(
                    userId = entity.userId,
                    type = entity.type,
                    language = entity.language
                )
            }
            .collectList()
            .flatMapMany { filteredEntities ->
                repository.saveAll(filteredEntities)
            }
    }
}