package com.backend.ord.features.bank.model

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.domain.persistence.dto.BankGroupDTO
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
