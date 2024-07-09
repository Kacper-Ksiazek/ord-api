package com.backend.ord.domain.dto

import java.util.*

data class BanksUsedInGameDTO(
    val id: UUID = UUID.randomUUID(),

    val game: GameDTO,
    val bank: BankDTO
)
