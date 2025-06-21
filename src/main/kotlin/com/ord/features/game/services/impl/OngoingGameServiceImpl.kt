package com.ord.features.game.services.impl

import com.ord.exceptions.REST.NotFoundException
import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.features.game.model.finished_game.extensions.getUserActivityType
import com.ord.features.game.model.ongoing_game.OngoingGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.enums.GameResult
import com.ord.features.game.model.ongoing_game.extensions.finish
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.services.FinishedGameService
import com.ord.features.game.services.OngoingGameService
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.service.UserActivityLogService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OngoingGameServiceImpl(
    override val repository: OngoingGameRepository,
    val userActivityLogService: UserActivityLogService,
    val ongoingGameMapper: OngoingGameMapper,
    val finishedGameService: FinishedGameService
) : OngoingGameService {

    @Transactional
    override fun completeGame(
        ongoingGameEntity: OngoingGameEntity,
        totalPoints: Int,
        duration: String
    ): FinishedGameEntity {
        return finishedGameService.save(
            ongoingGameEntity.finish(totalPoints, duration, GameResult.COMPLETED)
        ).let {
            val userId = it.user.id

            userActivityLogService.log(
                userId = userId,
                type = it.getUserActivityType(),
                language = it.language,
                difficulty = it.difficulty
            )

            this.deleteById(id = ongoingGameEntity.id, userId = userId)

            it
        }
    }

    @Transactional
    override fun completeGame(
        ongoingGame: OngoingGameDTO<*>,
        totalPoints: Int,
        duration: String
    ) {
        this.completeGame(
            ongoingGameEntity = ongoingGameMapper.toEntity(ongoingGame),
            totalPoints,
            duration
        )
    }

    @Transactional
    override fun cancelGame(
        ongoingGameId: UUID,
        userId: UUID,
        duration: String
    ) {
        this.findById(id = ongoingGameId, userId = userId)
            .let {
                if (it == null) {
                    throw NotFoundException("Ongoing game with ID $ongoingGameId not found for the current user")
                }

                finishedGameService.save(
                    it.finish(finalScore = 0, duration, result = GameResult.CANCELLED)
                )

                userActivityLogService.log(
                    userId = userId,
                    type = UserActivityType.GAME_QUIT,
                    language = it.language,
                    difficulty = it.difficulty
                )

                this.deleteById(id = it.id, userId = userId)
            }
    }
}