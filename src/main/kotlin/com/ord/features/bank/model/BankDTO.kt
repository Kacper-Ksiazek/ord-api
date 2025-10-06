package com.ord.features.bank.model

import com.ord.features.bank_group.model.BankGroupDTO
import java.time.Instant
import java.util.*

data class BankDTO(
    val id: UUID = UUID.randomUUID(),

    var name: String,
    var description: String,

    val userId: UUID,

    var group: BankGroupDTO? = null,
    var groupId: UUID? = group?.id,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
