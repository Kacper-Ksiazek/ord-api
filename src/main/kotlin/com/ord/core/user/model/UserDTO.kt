package com.ord.core.user.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import java.time.Instant
import java.util.*

data class UserDTO(
    val id: UUID,

    var name: String,
    var email: String,
    var password: String,
    var nativeLanguage: LanguageName,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
)