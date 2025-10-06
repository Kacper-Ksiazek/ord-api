package com.ord.features.game.services

import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.shared.services.UserResourceService
import reactor.core.publisher.Mono
import java.util.*

interface OngoingGameService : UserResourceService<OngoingGameEntity> {
    fun completeGame(
        ongoingGame: OngoingGameDTO<*>,
        score: Int,
        duration: String
    ): Mono<Void>

    fun cancelGame(
        ongoingGameId: UUID,
        userId: UUID,
        duration: String
    ): Mono<Void>
}