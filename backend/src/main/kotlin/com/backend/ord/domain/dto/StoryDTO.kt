package com.backend.ord.domain.dto

import java.time.Instant
import java.util.*

data class StoryDTO(
    val id: UUID = UUID.randomUUID(),

    var title: String,
    var content: String,
    var explanations: MutableMap<String, String> = mutableMapOf(),

    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
