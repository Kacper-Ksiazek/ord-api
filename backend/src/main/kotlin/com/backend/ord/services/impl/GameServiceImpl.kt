package com.backend.ord.services.impl

import com.backend.ord.domain.entities.Game
import com.backend.ord.enums.game.GameStatus
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.services.GameService
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

        return repository.save(game)
    }
}