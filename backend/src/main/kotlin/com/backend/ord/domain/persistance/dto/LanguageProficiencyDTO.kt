package com.backend.ord.domain.persistance.dto

import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.enums.persistance.language.LanguageProficiencyLevel
import java.time.Instant
import java.util.*

data class LanguageProficiencyDTO(
    val id: UUID = UUID.randomUUID(),

    var language: LanguageName,
    var proficiency: LanguageProficiencyLevel,
    var generativeContentLanguage: LanguageName,

    var user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
