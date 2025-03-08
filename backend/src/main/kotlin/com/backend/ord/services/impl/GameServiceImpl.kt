package com.backend.ord.services.impl

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.pivots.WordUsedInGame
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.repositories.GameRepository
import com.backend.ord.repositories.pivots.WordsUsedInGamesRepository
import com.backend.ord.services.GameService
import com.backend.ord.utils.data_classes.Percentage
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameServiceImpl(
    override val repository: GameRepository,
    val wordsUsedInGamesRepository: WordsUsedInGamesRepository
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

    override fun cancelGame(gameId: UUID, userId: UUID) {
        val affectedRows = repository.cancelGame(
            gameId = gameId,
            userId = userId
        )

        if (affectedRows == 0) {
            throw NotFoundException("User does not have a game with ID $gameId")
        }
    }
}
