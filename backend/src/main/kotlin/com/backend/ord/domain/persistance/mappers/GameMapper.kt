package com.backend.ord.domain.persistance.mappers

import com.backend.ord.domain.persistance.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistance.dto.game.GameDTOBase
import com.backend.ord.domain.persistance.entities.Game
import com.backend.ord.domain.persistance.mappers.bases.MapperBase

interface GameMapper : MapperBase<Game, GameDTOBase<*>> {
    fun toCrosswordDTO(entity: Game): CrosswordGameDTO
}