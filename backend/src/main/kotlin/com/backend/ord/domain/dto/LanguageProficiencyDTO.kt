package com.backend.ord.domain.dto

import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
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
