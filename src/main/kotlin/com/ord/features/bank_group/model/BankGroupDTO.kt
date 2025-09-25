package com.ord.features.bank_group.model

import com.ord.core.user.model.UserDTO
import java.time.Instant
import java.util.*

data class BankGroupDTO(
    val id: UUID = UUID.randomUUID(),

    var name: String,
    var color: String,

    val userId: UUID,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)