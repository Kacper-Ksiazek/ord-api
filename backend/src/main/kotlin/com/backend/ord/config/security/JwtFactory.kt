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
        user: User,
        response: HttpServletResponse
    ): String? {
        // Generate token
        val token = jwtService.generateToken(
            extraClaims = getExtraClaims(user),
            userDetails = user
        ).apply {
            createCookie(
                token = this,
                response = response
            )

            createNewUserSession(token = this)
        }

        return token
    }

    // ### Helpers
    private fun getExtraClaims(user: User): Map<String, Any> {
        val claimName = jwtProperties.userIdClaimName
        val claimValue = user.id.toString()

        return mapOf(claimName to claimValue)
    }

    private fun createCookie(token: String, response: HttpServletResponse) {
        createCookie(
            name = jwtProperties.authCookieName,
            value = token,
            response = response
        )
    }

    @Throws(UserNotFoundException::class)
    private fun createNewUserSession(token: String) {
        userSessionService.openSessionFromJWT(token)
    }
}
