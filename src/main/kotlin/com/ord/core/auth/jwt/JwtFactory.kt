package com.ord.core.auth.jwt

import com.ord.config.properties.JwtProperties
import com.ord.core.auth.services.UserSessionService
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.UserNotFoundException
import com.ord.shared.utils.CookieUtils
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
        user: UserEntity,
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
    private fun getExtraClaims(user: UserEntity): Map<String, Any> {
        val claimName = jwtProperties.userIdClaimName
        val claimValue = user.id.toString()

        return mapOf(claimName to claimValue)
    }

    private fun createCookie(token: String, response: HttpServletResponse) {
        CookieUtils.createCookie(
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