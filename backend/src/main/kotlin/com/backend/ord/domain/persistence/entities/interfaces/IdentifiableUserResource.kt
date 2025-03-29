package com.backend.ord.domain.persistence.entities.interfaces

import com.backend.ord.domain.persistence.entities.User
import java.util.*

interface IdentifiableUserResource {
    val id: UUID

    val user: User
}