package com.backend.ord.services.impl

import com.backend.ord.domain.entities.Game
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.services.GameService
import org.springframework.stereotype.Service

@Service
class GameServiceImpl(
    override val repository: UserResourceRepository<Game>
) : GameService