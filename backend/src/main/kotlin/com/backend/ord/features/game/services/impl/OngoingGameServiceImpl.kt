package com.backend.ord.features.game.services.impl

import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.features.game.model.finished_game.FinishedGame
import com.backend.ord.features.game.model.finished_game.extensions.getUserActivityType
import com.backend.ord.features.game.model.ongoing_game.OngoingGame
import com.backend.ord.features.game.model.ongoing_game.OngoingGameDTO
import com.backend.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.backend.ord.features.game.model.ongoing_game.enums.GameResult
import com.backend.ord.features.game.model.ongoing_game.extensions.finish
import com.backend.ord.features.game.repositories.OngoingGameRepository
import com.backend.ord.features.game.services.FinishedGameService
import com.backend.ord.features.game.services.OngoingGameService
import com.backend.ord.features.user_activity_log.model.enums.UserActivityType
import com.backend.ord.features.user_activity_log.service.UserActivityLogService
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
        ongoingGame: OngoingGame,
        totalPoints: Int,
        duration: String
    ): FinishedGame {
        return finishedGameService.save(
            ongoingGame.finish(totalPoints, duration, GameResult.COMPLETED)
        ).let {
            val userId = it.user.id

            userActivityLogService.log(
                userId = userId,
                type = it.getUserActivityType(),
                language = it.language,
                difficulty = it.difficulty
            )

            this.deleteById(id = ongoingGame.id, userId = userId)

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
            ongoingGame = ongoingGameMapper.toEntity(ongoingGame),
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