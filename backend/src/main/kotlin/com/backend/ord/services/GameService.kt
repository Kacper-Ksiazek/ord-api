package com.backend.ord.services

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.services.bases.UserResourceService

interface GameService : UserResourceService<Game> {
    fun finishGame(
        game: Game,
        finalScore: Int,
        duration: String
    ): Game
}