package com.backend.ord.config.security.jwt

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.core.auth.jwt.JwtFactory
import com.backend.ord.core.auth.jwt.JwtService
import com.backend.ord.core.auth.services.UserSessionService
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.service.UserService
import com.backend.ord.exceptions.NoCorrespondingUserSessionException
import com.backend.ord.exceptions.UserNotFoundException
import com.backend.ord.shared.utils.CookieUtils
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val jwtFactory: JwtFactory,
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
    private val jwtTokenValidator: JwtTokenValidator,
    private val userService: UserService,
    private val userDetailsService: UserDetailsService,
    private val userSessionService: UserSessionService
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestWithMutableAuthCookie = RequestWithMutableAuthCookie(
            request,
            jwtProperties.authCookieName
        )

        // Check if the authentication cookie has been received
        jwtService.getJWTFromRequest(request)?.let { jwtToken: String ->
            // Authenticate the user using the JWT token
            handleJwtAuthenticationFilter(
                jwtToken = jwtToken,
                request = request,
                response = response,
                requestWithMutableAuthCookie = requestWithMutableAuthCookie
            )
        }

        // Continue to the next filter
        filterChain.doFilter(request, response)
    }

    private fun handleJwtAuthenticationFilter(
        jwtToken: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        requestWithMutableAuthCookie: RequestWithMutableAuthCookie
    ) {
        try {
            // Validate the JWT token
            jwtTokenValidator.validateCorrespondingSession(jwtToken)

            // Extract the user's email from the JWT token
            val userEmail = jwtService.extractUsername(jwtToken)

            // Check if the user is currently authenticated
            if (checkIfUserIsNotAuthenticated()) {
                val userDetails = getUserDetails(jwtToken)

                // Check if the JWT token is valid
                if (jwtTokenValidator.validate(jwtToken, userDetails)) {
                    updateSecurityContext(userDetails, request)
                }
            }
        } // If either the user is not found or there is no corresponding session, delete the cookie
        catch (e: NoCorrespondingUserSessionException) {
            deleteCookieWithToken(response)
        } // If the session has expired, renew it
        catch (e: UsernameNotFoundException) {
            deleteCookieWithToken(response)
        } catch (e: ExpiredJwtException) {
            // Get the user associated with the expired JWT token
            userService.findUserByAuthToken(jwtToken)!!.let { user: UserEntity ->
                // Generate a new JWT token for the user
                renewUserSession(
                    user = user,
                    request = request,
                    response = response,
                    requestWithMutableAuthCookie = requestWithMutableAuthCookie
                )

                // Remove previous session from the database
                removePreviousSession(jwtToken = jwtToken)
            }
        }
    }

    private fun removePreviousSession(jwtToken: String) {
        userSessionService.deleteSessionByToken(jwtToken)
    }

    private fun renewUserSession(
        user: UserEntity,
        request: HttpServletRequest,
        response: HttpServletResponse,
        requestWithMutableAuthCookie: RequestWithMutableAuthCookie
    ) {
        try {
            // Generate a new JWT token for the user
            val newJwtToken = jwtFactory.createTokenForUser(user, response)

            // Update the SecurityContext with the user's details
            updateSecurityContext(user.email, request)

            // Update the cookie header in the request in order for subsequent filters and controllers to use the new token
            requestWithMutableAuthCookie.authCookieValue = newJwtToken
        } catch (e: UserNotFoundException) {
            return
        }
    }

    private fun deleteCookieWithToken(response: HttpServletResponse) {
        CookieUtils.deleteCookie(jwtProperties.authCookieName, response)
    }

    private fun getUserDetails(jwtToken: String): UserDetails {
        return userDetailsService.loadUserByUsername(
            jwtService.extractUsername(jwtToken)
        )
    }

    private fun checkIfUserIsNotAuthenticated(): Boolean {
        return SecurityContextHolder.getContext().authentication == null
    }

    private fun updateSecurityContext(
        userEmail: String,
        request: HttpServletRequest
    ) {
        // Get the user's details
        val userDetails = userDetailsService.loadUserByUsername(userEmail)

        // Authenticate the user
        updateSecurityContext(userDetails, request)
    }

    private fun updateSecurityContext(
        userDetails: UserDetails,
        request: HttpServletRequest
    ) {
        // Set the authentication token
        val authToken = UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        )

        // Set the authentication token's details
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

        // Update the SecurityContext with the user's details
        SecurityContextHolder.getContext().authentication = authToken
    }
}