package com.ord.features.game.repositories

import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FinishedGameRepository :
    UserResourceRepository<FinishedGameEntity>,
    ReactiveCrudRepository<FinishedGameEntity, UUID>