package com.backend.ord.domain.persistence.dto

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.enums.persistence.language.LanguageName
import java.time.Instant
import java.util.*

data class QuicklyAddedWordDTO(
    val id: UUID = UUID.randomUUID(),

    var word: String,
    var language: LanguageName,

    var user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
