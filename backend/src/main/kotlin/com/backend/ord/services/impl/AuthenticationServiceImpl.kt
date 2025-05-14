package com.backend.ord.services.impl

import com.backend.ord.api.requests.LoginRequest
import com.backend.ord.api.requests.RegisterRequest
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.config.security.JwtFactory
import com.backend.ord.config.security.JwtService
import com.backend.ord.core.user.UserRepository
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.model.enums.UserRole
import com.backend.ord.exceptions.ForbiddenException
import com.backend.ord.exceptions.UserNotFoundException
import com.backend.ord.services.AuthenticationService
import com.backend.ord.services.UserSessionService
import com.backend.ord.utils.CookieUtils.deleteCookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl(
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtFactory: JwtFactory,
    private val userRepository: UserRepository,
    private val userSessionService: UserSessionService
) : AuthenticationService {

    @Throws(UserNotFoundException::class)
    override fun register(
        request: RegisterRequest,
        response: HttpServletResponse
    ): UserEntity {
        // Save user to database
        val user = userRepository.save( // Create user object
            UserEntity(
                name = request.name,
                email = request.email,
                password = passwordEncoder.encode(request.password),
                role = UserRole.USER,
                nativeLanguage = request.nativeLanguage
            )
        )

        // Generate and assign a JWT token
        jwtFactory.createTokenForUser(user, response)

        return user
    }


    @Throws(UserNotFoundException::class)
    override fun login(
        request: LoginRequest,
        response: HttpServletResponse
    ): UserEntity {
        val user = userRepository.findByEmail(request.email)
            ?: throw UserNotFoundException(email = request.email)

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        jwtFactory.createTokenForUser(user, response)

        return user
    }

    @Throws(ForbiddenException::class)
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val token = jwtService
            .getJWTFromRequest(request)
            ?: throw ForbiddenException("No JWT token found in request")

        // Delete session from database
        userSessionService.deleteSessionByToken(token)

        // Delete JWT cookie
        deleteCookie(jwtProperties.authCookieName, response)
    }
}
