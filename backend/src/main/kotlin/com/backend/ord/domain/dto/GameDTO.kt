package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase
import com.backend.ord.enums.Game.GameStatus
import com.backend.ord.enums.Game.GameType

data class GameDTO(
    var finalScore: Int = 0,
    var acquiredPoints: Int = 0,
    var type: GameType,
    var status: GameStatus = GameStatus.IN_PROGRESS,

    val user: UserDTO
) : DTOBase()
