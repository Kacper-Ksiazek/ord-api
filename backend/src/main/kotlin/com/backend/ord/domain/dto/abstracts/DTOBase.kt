package com.backend.ord.domain.dto.abstracts

import java.time.Instant
import java.util.*

abstract class DTOBase(
    val id: UUID = UUID.randomUUID(),
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
