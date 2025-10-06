package com.ord.core.security

import com.ord.exceptions.REST.NotFoundException

/**
 * Custom exception thrown when a JWT token is expired and there is no corresponding
 * valid session in the database. This indicates that the auth cookies should be cleared
 * for security reasons.
 */
class MissingUserSessionException(
    message: String = "JWT token is expired and no valid session exists"
) : NotFoundException(message)