package com.ord.exceptions

import com.ord.exceptions.REST.NotFoundException
import java.util.*

class UserNotFoundException(
    userId: UUID? = null,
    email: String? = null,
) : NotFoundException(
    userId?.let { "User with id $it not found" } ?: "User with email $email not found"
)
