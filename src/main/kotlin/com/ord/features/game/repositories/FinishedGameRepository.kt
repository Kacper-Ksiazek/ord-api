package com.ord.features.game.repositories

import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface FinishedGameRepository : UserResourceRepository<FinishedGameEntity>