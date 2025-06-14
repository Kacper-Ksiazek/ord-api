package com.backend.ord.exceptions

import jakarta.validation.constraints.Email
import java.util.*

class UserNotFoundException(
    userId: UUID? = null,
    email: String? = null,
) : Exception(
    userId?.let { "User with id $it not found" } ?: "User with email $email not found"
)
