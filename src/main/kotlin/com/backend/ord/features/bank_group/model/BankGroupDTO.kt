package com.backend.ord.features.bank_group.model

import com.backend.ord.core.user.model.UserDTO
import java.time.Instant
import java.util.*

data class BankGroupDTO(
    val id: UUID = UUID.randomUUID(),

    var name: String,
    var color: String,

    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)