package com.ord.shared.models

import com.ord.core.user.model.UserEntity
import java.util.*

interface IdentifiableUserResource {
    val id: UUID

    val userId: UUID
    val user: UserEntity?
}