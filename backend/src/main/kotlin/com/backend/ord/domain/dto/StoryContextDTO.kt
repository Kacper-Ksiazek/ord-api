package com.backend.ord.domain.dto

import com.backend.ord.enums.persistance.StoryContextType
import java.time.Instant
import java.util.*

data class StoryContextDTO(
    val id: UUID = UUID.randomUUID(),

    var title: String,
    var type: StoryContextType,
    var prompt: String,

    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
)
