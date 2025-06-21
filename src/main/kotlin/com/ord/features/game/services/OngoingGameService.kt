package com.ord.features.game.services

import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.shared.services.UserResourceService
import java.util.*

interface OngoingGameService : UserResourceService<OngoingGameEntity> {
    fun completeGame(
        ongoingGameEntity: OngoingGameEntity,
        totalPoints: Int,
        duration: String
    ): FinishedGameEntity

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