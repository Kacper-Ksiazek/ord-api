package com.ord.features.game.services.impl

import com.ord.features.game.model.finished_game.extensions.getUserActivityType
import com.ord.features.game.model.ongoing_game.OngoingGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.extensions.cancel
import com.ord.features.game.model.ongoing_game.extensions.finish
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.services.FinishedGameService
import com.ord.features.game.services.OngoingGameService
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.service.UserActivityLogService
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import java.util.*

@Service
class OngoingGameServiceImpl(
    val ongoingGameRepository: OngoingGameRepository,
    val userActivityLogService: UserActivityLogService,
    val ongoingGameMapper: OngoingGameMapper,
    val finishedGameService: FinishedGameService,
    val transactionalOperator: TransactionalOperator
) : OngoingGameService {
    override val repository: OngoingGameRepository = ongoingGameRepository

    override fun completeGame(
        ongoingGame: OngoingGameDTO<*>,
        score: Int,
        duration: String
    ): Mono<Void> {
        return finishedGameService
            .save(ongoingGame.finish(score, duration))
            .flatMap { finishedGame ->
                userActivityLogService
                    .log(
                        userId = finishedGame.userId,
                        type = finishedGame.getUserActivityType(),
                        language = finishedGame.language,
                        difficulty = finishedGame.difficulty
                    )
                    .flatMap { repository.deleteById(ongoingGame.id) }
            }
            .`as`(transactionalOperator::transactional)
            .then()
    }

    override fun cancelGame(
        ongoingGameId: UUID,
        userId: UUID,
        duration: String
    ): Mono<Void> {
        return findByIdOrFail(id = ongoingGameId, userId = userId)
            .map { ongoingGameMapper.toDTO(it) }
            .flatMap { ongoingGame ->
                finishedGameService.save(ongoingGame.cancel(duration))
                    .flatMap { finishedGame ->
                        userActivityLogService.log(
                            userId = userId,
                            type = UserActivityType.GAME_QUIT,
                            language = finishedGame.language,
                            difficulty = finishedGame.difficulty
                        )
                            .flatMap {
                                repository.deleteById(ongoingGameId).then()
                            }
                    }
            }
            .`as`(transactionalOperator::transactional)
    }
}