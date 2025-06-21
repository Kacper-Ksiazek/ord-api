package com.ord.features.story.models.dto

import com.ord.core.user.model.UserDTO
import com.ord.features.story.models.enums.StoryContextType
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
