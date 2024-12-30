package com.backend.ord.services.impl

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.services.GameService
import com.backend.ord.utils.data_classes.Percentage
import org.springframework.stereotype.Service

@Service
class GameServiceImpl(
    override val repository: UserResourceRepository<Game>
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
}