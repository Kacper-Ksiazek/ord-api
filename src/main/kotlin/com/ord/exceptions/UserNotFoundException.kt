package com.ord.exceptions

import java.util.*

class UserNotFoundException(
    userId: UUID? = null,
    email: String? = null,
) : Exception(
    userId?.let { "User with id $it not found" } ?: "User with email $email not found"
)
