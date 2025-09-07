package com.ord.features.game.services.impl

import com.ord.features.game.model.finished_game.FinishedGameEntity
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
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import java.util.*

@Service
class OngoingGameServiceImpl(
    override val repository: OngoingGameRepository,
    val userActivityLogService: UserActivityLogService,
    val ongoingGameMapper: OngoingGameMapper,
    val finishedGameService: FinishedGameService,
    val transactionalOperator: TransactionalOperator
) : OngoingGameService {

    override fun completeGame(
        ongoingGameEntity: OngoingGameEntity,
        score: Int,
        duration: String
    ): Mono<FinishedGameEntity> {
        return finishedGameService
            .save(ongoingGameEntity.finish(score, duration))
            .flatMap { finishedGame ->
                val userId = finishedGame.userId

                userActivityLogService.log(
                    userId = userId,
                    type = finishedGame.getUserActivityType(),
                    language = finishedGame.language,
                    difficulty = finishedGame.difficulty
                )
                    .flatMap {
                        repository.deleteById(ongoingGameEntity.id)
                            .thenReturn(finishedGame)
                    }
            }
            .`as`(transactionalOperator::transactional)
    }

    override fun completeGame(
        ongoingGame: OngoingGameDTO<*>,
        score: Int,
        duration: String
    ): Mono<Void> {
        return this.completeGame(
            ongoingGameEntity = ongoingGameMapper.toEntity(ongoingGame),
            score,
            duration
        ).then()
    }

    override fun cancelGame(
        ongoingGameId: UUID,
        userId: UUID,
        duration: String
    ): Mono<Void> {
        return this.findByIdOrFail(id = ongoingGameId, userId = userId)
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
                                repository.deleteById(ongoingGame.id).then()
                            }
                    }
            }
            .`as`(transactionalOperator::transactional)
    }
}