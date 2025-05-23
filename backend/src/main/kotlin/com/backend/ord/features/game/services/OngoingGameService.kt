package com.backend.ord.features.game.services

import com.backend.ord.domain.persistence.entities.FinishedGame
import com.backend.ord.features.game.model.OngoingGame
import com.backend.ord.features.game.model.OngoingGameDTO
import com.backend.ord.shared.services.UserResourceService
import java.util.*

interface OngoingGameService : UserResourceService<OngoingGame> {
    fun completeGame(
        ongoingGame: OngoingGame,
        totalPoints: Int,
        duration: String
    ): FinishedGame

    fun completeGame(
        ongoingGame: OngoingGameDTO<*>,
        totalPoints: Int,
        duration: String
    )

    fun cancelGame(
        ongoingGameId: UUID,
        userId: UUID,
        duration: String
    )
}