package com.backend.ord.features.game.services

import com.backend.ord.features.game.model.finished_game.FinishedGame
import com.backend.ord.features.game.model.ongoing_game.OngoingGameDTO
import com.backend.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.backend.ord.shared.services.UserResourceService
import java.util.*

interface OngoingGameService : UserResourceService<OngoingGameEntity> {
    fun completeGame(
        ongoingGameEntity: OngoingGameEntity,
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