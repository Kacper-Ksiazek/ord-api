package com.ord.core.langugae_proficiency.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserDTO
import java.time.Instant
import java.util.*

data class LanguageProficiencyDTO(
    val id: UUID,

    val language: LanguageName,
    var level: LanguageProficiencyLevel,
    var generativeContentLanguage: LanguageName,

    val userId: UUID,
    var user: UserDTO? = null,

    val createdAt: Instant = Instant.now(),
)