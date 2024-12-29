package com.backend.ord.domain.persistance.entities.interfaces

import com.backend.ord.domain.persistance.entities.User
import java.util.*

interface IdentifiableUserResource {
    val id: UUID

    val user: User
}