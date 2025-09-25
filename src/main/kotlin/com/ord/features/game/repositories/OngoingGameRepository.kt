package com.ord.features.game.repositories

import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface OngoingGameRepository :
    UserResourceRepository<OngoingGameEntity>,
    ReactiveCrudRepository<OngoingGameEntity, UUID>