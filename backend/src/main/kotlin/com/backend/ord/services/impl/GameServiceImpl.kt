package com.backend.ord.services.impl

import com.backend.ord.domain.persistence.entities.FinishedGame
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.features.ongoing_game.model.OngoingGame
import com.backend.ord.features.ongoing_game.model.OngoingGameDTO
import com.backend.ord.features.ongoing_game.model.OngoingGameMapper
import com.backend.ord.features.ongoing_game.model.enums.GameGrade
import com.backend.ord.features.ongoing_game.model.enums.GameResult
import com.backend.ord.features.ongoing_game.model.enums.GameType
import com.backend.ord.features.ongoing_game.model.finish
import com.backend.ord.features.ongoing_game.service.OngoingGameService
import com.backend.ord.services.FinishedGameService
import com.backend.ord.services.GameService
import com.backend.ord.services.UserActivityLogService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private fun FinishedGame.getUserActivityType(): UserActivityType {
    return when (type) {
        GameType.CROSSWORD -> when (grade) {
            GameGrade.S -> UserActivityType.CROSSWORD_GAME_COMPLETED_FLAWLESSLY
            else -> UserActivityType.CROSSWORD_GAME_COMPLETED_WITH_MISTAKES
        }

        GameType.WORDS_TYPING -> when (grade) {
            GameGrade.S -> UserActivityType.WORDS_TYPING_GAME_COMPLETED_FLAWLESSLY
            else -> UserActivityType.WORDS_TYPING_GAME_COMPLETED_WITH_MISTAKES
        }

        else -> throw UnsupportedOperationException()
    }
}

@Service
class GameServiceImpl(
    val ongoingGameService: OngoingGameService,
    val userActivityLogService: UserActivityLogService,
    val finishedGameService: FinishedGameService,
    val ongoingGameMapper: OngoingGameMapper,
) : GameService {

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

            ongoingGameService.deleteById(id = ongoingGame.id, userId = userId)

            it
        }
    }

    override fun completeGame(
        ongoingGame: OngoingGameDTO<*>,
        totalPoints: Int,
        duration: String
    ) {
        completeGame(
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
        ongoingGameService
            .findById(id = ongoingGameId, userId = userId)
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

                ongoingGameService.deleteById(id = it.id, userId = userId)
            }


    }
}
