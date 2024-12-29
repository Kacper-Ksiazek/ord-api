package com.backend.ord.domain.persistence.mappers

import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.dto.game.GameDTOBase
import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.mappers.bases.MapperBase

interface GameMapper : MapperBase<Game, GameDTOBase<*>> {
    fun toCrosswordDTO(entity: Game): CrosswordGameDTO
}