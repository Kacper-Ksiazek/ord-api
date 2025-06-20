package com.backend.ord.features.game.repositories

import com.backend.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface OngoingGameRepository : UserResourceRepository<OngoingGameEntity>