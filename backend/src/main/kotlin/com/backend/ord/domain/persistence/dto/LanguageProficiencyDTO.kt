package com.backend.ord.domain.persistence.dto

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
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
