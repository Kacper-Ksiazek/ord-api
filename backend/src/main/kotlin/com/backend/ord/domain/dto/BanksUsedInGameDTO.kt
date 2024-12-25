package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.game.GameDTOBase
import java.util.*

data class BanksUsedInGameDTO(
    val id: UUID = UUID.randomUUID(),

    val game: GameDTOBase<*>,
    val bank: BankDTO
)
