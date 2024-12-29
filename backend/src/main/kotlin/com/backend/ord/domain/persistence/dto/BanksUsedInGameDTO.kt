package com.backend.ord.domain.persistence.dto

import com.backend.ord.domain.persistence.dto.game.GameDTOBase
import java.util.*

data class BanksUsedInGameDTO(
    val id: UUID = UUID.randomUUID(),

    val game: GameDTOBase<*>,
    val bank: BankDTO
)
