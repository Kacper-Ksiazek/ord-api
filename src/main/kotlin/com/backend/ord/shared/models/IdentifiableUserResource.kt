package com.backend.ord.shared.models

import com.backend.ord.core.user.model.UserEntity
import java.util.*

interface IdentifiableUserResource {
    val id: UUID

    val user: UserEntity
}