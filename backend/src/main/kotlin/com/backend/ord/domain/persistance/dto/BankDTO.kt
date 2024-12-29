package com.backend.ord.domain.persistance.dto

import java.time.Instant
import java.util.*

data class BankDTO(
    val id: UUID = UUID.randomUUID(),

    var name: String,
    var description: String,

    val user: UserDTO,

    var bankGroup: BankGroupDTO? = null,
    var bankGroupId: UUID? = bankGroup?.id,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
