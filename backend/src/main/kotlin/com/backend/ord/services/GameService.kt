package com.backend.ord.services

import com.backend.ord.domain.persistence.entities.FinishedGame
import com.backend.ord.domain.persistence.entities.OngoingGame
import java.util.*

interface GameService {
    fun completeGame(
        game: OngoingGame,
        finalScore: Int,
        duration: String
    ): FinishedGame

    fun cancelGame(
        gameId: UUID,
        userId: UUID,
        duration: String
    )
}