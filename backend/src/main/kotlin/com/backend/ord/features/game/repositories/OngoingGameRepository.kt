package com.backend.ord.features.game.repositories

import com.backend.ord.features.game.model.OngoingGame
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface OngoingGameRepository : UserResourceRepository<OngoingGame>