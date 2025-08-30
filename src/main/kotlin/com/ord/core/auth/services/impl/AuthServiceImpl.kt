package com.ord.core.auth.services.impl

import com.ord.config.properties.JwtProperties
import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.auth.jwt.JwtFactory
import com.ord.core.auth.jwt.JwtService
import com.ord.core.auth.services.AuthService
import com.ord.core.auth.services.UserSessionService
import com.ord.core.user.UserRepository
import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.enums.UserRole
import com.ord.exceptions.ForbiddenException
import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.REST.NotFoundException
import com.ord.exceptions.UserNotFoundException
import com.ord.shared.utils.CookieUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtFactory: JwtFactory,
    private val userRepository: UserRepository,
    private val userSessionService: UserSessionService
) : AuthService {
    override fun register(
        request: RegisterRequest,
        response: HttpServletResponse
    ): UserEntity {
        // Save user to database
        val user = try {
            userRepository.save( // Create user object
                UserEntity(
                    name = request.name,
                    email = request.email,
                    password = passwordEncoder.encode(request.password),
                    role = UserRole.USER,
                    nativeLanguage = request.nativeLanguage
                )
            )
        } catch (ex: DataIntegrityViolationException) {
            throw BadRequestException("User already exists")
        }

        // Generate and assign a JWT token
        jwtFactory.createTokenForUser(user, response)

        return user
    }

    override fun login(
        request: LoginRequest,
        response: HttpServletResponse
    ): UserEntity {
        return try {
            val user: UserEntity = userRepository
                .findByEmail(request.email) ?: throw BadCredentialsException("User does not exist")

            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.email,
                    request.password
                )
            )

            jwtFactory.createTokenForUser(user, response)

            user
        } catch (e: BadCredentialsException) {
            throw NotFoundException("Invalid credentials")
        }

    }

    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val token = jwtService
            .getJWTFromRequest(request)
            ?: throw ForbiddenException("No JWT token found in request")

        userSessionService.deleteSessionByToken(token)

        CookieUtils.deleteCookie(jwtProperties.authCookieName, response)
    }
}