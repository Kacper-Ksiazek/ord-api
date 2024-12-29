package com.backend.ord.repositories

import com.backend.ord.domain.persistance.entities.Game
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : UserResourceRepository<Game>