package com.backend.ord.domain.persistence.entities.interfaces

import com.backend.ord.core.user.model.UserEntity
import java.util.*

interface IdentifiableUserResource {
    val id: UUID

    val user: UserEntity
}