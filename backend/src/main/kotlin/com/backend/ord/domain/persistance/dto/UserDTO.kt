package com.backend.ord.domain.persistance.dto

import com.backend.ord.enums.persistance.UserRole
import com.backend.ord.enums.persistance.language.LanguageName
import java.time.Instant
import java.util.*

data class UserDTO(
    val id: UUID = UUID.randomUUID(),

    var name: String,
    var email: String,
    var role: UserRole = UserRole.USER,
    var password: String,
    var nativeLanguage: LanguageName,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
)