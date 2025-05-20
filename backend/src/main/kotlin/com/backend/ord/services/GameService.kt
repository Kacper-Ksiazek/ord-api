package com.backend.ord.services

import com.backend.ord.domain.persistence.entities.FinishedGame
import com.backend.ord.features.ongoing_game.model.OngoingGame
import com.backend.ord.features.ongoing_game.model.OngoingGameDTO
import java.util.*

interface GameService {
    fun completeGame(
        game: OngoingGame,
        totalPoints: Int,
        duration: String
    ): FinishedGame

    fun completeGame(
        game: OngoingGameDTO<*>,
        totalPoints: Int,
        duration: String
    )

    fun cancelGame(
        gameId: UUID,
        userId: UUID,
        duration: String
    )
}