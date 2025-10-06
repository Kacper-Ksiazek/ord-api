package com.ord.features.game.services.impl

import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.services.FinishedGameService
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FinishedGameServiceImpl(
    val finishedGameRepository: FinishedGameRepository
) : FinishedGameService {
    override val repository: FinishedGameRepository = finishedGameRepository
}