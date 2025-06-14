package com.backend.ord.features.game.repositories

import com.backend.ord.features.game.model.finished_game.FinishedGameEntity
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface FinishedGameRepository : UserResourceRepository<FinishedGameEntity>