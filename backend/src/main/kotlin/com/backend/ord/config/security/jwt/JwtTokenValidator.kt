package com.backend.ord.config.security.jwt

import com.backend.ord.core.auth.jwt.JwtService
import com.backend.ord.core.auth.services.UserSessionService
import com.backend.ord.exceptions.NoCorrespondingUserSessionException
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenValidator(
    private val jwtService: JwtService,
    private val userSessionService: UserSessionService
) {
    @Throws(NoCorrespondingUserSessionException::class)
    fun validate(
        jwtToken: String,
        userDetails: UserDetails
    ): Boolean {
        val username = jwtService.extractUsername(jwtToken)

        // Check if the token has a corresponding session
        validateCorrespondingSession(jwtToken)

        // Check if the token is expired
        validateTokenExpiration(jwtToken)

        // Check if the token's username matches the user's username
        return userDetails.username.equals(username, ignoreCase = true)
    }

    @Throws(NoCorrespondingUserSessionException::class)
    fun validateCorrespondingSession(jwtToken: String) {
        if (isTokenMissingCorrespondingSession(jwtToken)) {
            throw NoCorrespondingUserSessionException("No corresponding session found for the user")
        }
    }

    @Throws(ExpiredJwtException::class)
    fun validateTokenExpiration(jwtToken: String) {
        if (isTokenExpired(jwtToken)) {
            throw ExpiredJwtException(null, null, "Token has expired")
        }
    }

    private fun isTokenMissingCorrespondingSession(jwtToken: String): Boolean {
        val userId = jwtService.extractUserId(jwtToken)
        val correspondingSession = userSessionService.findByTokenAndUserId(jwtToken, userId)

        return correspondingSession == null
    }

    private fun isTokenExpired(jwtToken: String): Boolean {
        val expirationDate = jwtService.extractExpiration(jwtToken)

        return expirationDate.before(Date())
    }
}