package com.ord.core.security

import com.ord.exceptions.REST.NotFoundException

/**
 * Thrown when the auth cookie is missing a matching, unexpired server-side session.
 * [SessionSecurityContextRepository] clears the cookie and the request is treated as anonymous (401).
 */
class MissingUserSessionException(
    message: String = "No valid session exists"
) : NotFoundException(message)
