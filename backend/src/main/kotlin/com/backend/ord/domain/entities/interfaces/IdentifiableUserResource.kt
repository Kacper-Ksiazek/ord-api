package com.backend.ord.domain.entities.interfaces

import com.backend.ord.domain.entities.User
import java.util.*

interface IdentifiableUserResource {
    val id: UUID

    val user: User
}