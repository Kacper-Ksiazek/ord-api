package com.backend.ord.exceptions

import jakarta.validation.constraints.Email
import lombok.NoArgsConstructor
import java.util.*

@NoArgsConstructor
class UserNotFoundException(
    userId: UUID? = null,
    email: String? = null,
) : Exception(
    userId?.let { "User with id $it not found" } ?: "User with email $email not found"
)
