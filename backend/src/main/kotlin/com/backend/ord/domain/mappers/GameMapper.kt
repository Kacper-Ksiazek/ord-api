package com.backend.ord.domain.mappers

import com.backend.ord.domain.dto.game.CrosswordGameDTO
import com.backend.ord.domain.dto.game.GameDTOBase
import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.mappers.bases.MapperBase

interface GameMapper : MapperBase<Game, GameDTOBase<*>> {
    fun toCrosswordDTO(entity: Game): CrosswordGameDTO
}