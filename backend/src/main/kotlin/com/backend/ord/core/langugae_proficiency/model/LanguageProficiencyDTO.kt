package com.backend.ord.core.langugae_proficiency.model

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.backend.ord.core.user.model.UserDTO
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