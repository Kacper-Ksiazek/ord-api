package com.backend.ord.features.quickly_added_words.model

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserDTO
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
