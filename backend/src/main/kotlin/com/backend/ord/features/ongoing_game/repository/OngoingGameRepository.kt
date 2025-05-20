package com.backend.ord.features.ongoing_game.repository

import com.backend.ord.features.ongoing_game.model.OngoingGame
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface OngoingGameRepository : UserResourceRepository<OngoingGame>