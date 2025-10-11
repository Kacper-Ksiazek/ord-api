package com.ord.core.user.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import java.time.Instant
import java.util.*

data class UserDTO(
    val id: UUID,

    var name: String,
    var email: String,
    var nativeLanguage: LanguageName,
    var selectedLearningLanguage: LanguageName? = null,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
)