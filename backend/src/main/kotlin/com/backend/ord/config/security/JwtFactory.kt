package com.backend.ord.config.security

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.domain.entities.*
import com.backend.ord.exceptions.UserNotFoundException
import com.backend.ord.services.UserSessionService
import com.backend.ord.utils.CookieUtils.createCookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class JwtFactory(
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
    private val userSessionService: UserSessionService
) {
    @Throws(UserNotFoundException::class)
    fun createTokenForUser(
        user: User?,
        response: HttpServletResponse
    ): String? {
        // Generate token
        val token = jwtService.generateToken(
            getExtraClaims(user),
            user
        )

        // Create cookie
        createCookie(token, response)

        // Create new user session
        createNewUserSession(token)

        // Return generated JWT token
        return token
    }

    // ### Helpers
    private fun getExtraClaims(user: User?): Map<String?, Any?> {
        val claimName = jwtProperties.userIdClaimName

        return java.util.Map.of<String?, Any?>(
            claimName, user.getId()
        )
    }

    private fun createCookie(token: String?, response: HttpServletResponse) {
        val cookieName = jwtProperties.authCookieName
        createCookie(cookieName, token, response)
    }

    @Throws(UserNotFoundException::class)
    private fun createNewUserSession(token: String?) {
        userSessionService.openSessionFromJWT(token)
    }
}
