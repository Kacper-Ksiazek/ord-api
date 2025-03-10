package com.backend.ord.services.impl

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.pivots.WordUsedInGame
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.repositories.GameRepository
import com.backend.ord.repositories.pivots.WordsUsedInGamesRepository
import com.backend.ord.services.GameService
import com.backend.ord.services.UserActivityLogService
import com.backend.ord.utils.data_classes.Percentage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private fun Game.getUserActivityType(): UserActivityType {
    return when (type) {
        GameType.CROSSWORD -> when (grade) {
            GameGrade.S -> UserActivityType.CROSSWORD_GAME_COMPLETED_FLAWLESSLY
            else -> UserActivityType.CROSSWORD_GAME_COMPLETED_WITH_MISTAKES
        }

        else -> throw IllegalArgumentException("User activity type not defined for game type $this")
    }
}

@Service
class GameServiceImpl(
    override val repository: GameRepository,
    val wordsUsedInGamesRepository: WordsUsedInGamesRepository,
    val userActivityLogService: UserActivityLogService
) : GameService {
    override fun finishGame(
        game: Game,
        finalScore: Int,
        duration: String
    ): Game {
        game.finalScore = finalScore
        game.duration = duration

        game.status = GameStatus.COMPLETED
        game.grade = GameGrade.fromPercentage(Percentage(finalScore))

        // TODO: Test this & adjust existing tests
        userActivityLogService.log(
            userId = game.user.id,
            type = game.getUserActivityType(),
            language = game.language,
            difficulty = game.difficulty
        )

        return repository.save(game)
    }

    override fun saveAllWordsUsedInAGame(
        wordsIds: Set<UUID>,
        gameId: UUID
    ) {
        wordsUsedInGamesRepository.saveAll(
            wordsIds.map {
                WordUsedInGame(
                    gameId = gameId,
                    wordId = it
                )
            }
        )
    }

    @Transactional
    override fun cancelGame(gameId: UUID, userId: UUID) {
        val game = repository.findOneForUser(id = gameId, userId = userId)

        if (game == null) {
            throw NotFoundException("Game with ID $gameId not found for the current user")
        }

        if (game.status == GameStatus.CANCELED) {
            throw BadRequestException("Game with ID $gameId is already canceled")
        }

        game.status = GameStatus.CANCELED
        repository.save(game)

        userActivityLogService.log(
            userId = userId,
            type = UserActivityType.GAME_QUIT,
            language = game.language,
            difficulty = game.difficulty
        )
    }
}
