package com.backend.ord.domain.dto

import java.time.Instant
import java.util.*

data class BankGroupDTO(
    val id: UUID = UUID.randomUUID(),

    var name: String,
    var color: String,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
