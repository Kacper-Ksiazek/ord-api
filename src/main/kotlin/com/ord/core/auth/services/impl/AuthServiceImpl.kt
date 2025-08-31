package com.ord.core.auth.services.impl

import com.ord.config.properties.JwtProperties
import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.auth.models.UserSessionEntity
import com.ord.core.auth.services.AuthService
import com.ord.core.security.JwtService
import com.ord.core.security.UserRepositoryReactive
import com.ord.core.security.UserSessionRepositoryReactive
import com.ord.core.security.addAuthTokenCookie
import com.ord.core.security.getCookieValue
import com.ord.core.security.invalidateAuthTokenCookie
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.UserMapper
import com.ord.core.user.model.enums.UserRole
import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.REST.NotFoundException
import com.ord.exceptions.REST.UnauthorizedException
import io.jsonwebtoken.Claims
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class AuthServiceImpl(
    private val jwtProperties: JwtProperties,
    private val jwtService: JwtService,
    private val encoder: PasswordEncoder,
    private val userMapper: UserMapper,

    private val userRepositoryReactive: UserRepositoryReactive,
    private val sessionRepositoryReactive: UserSessionRepositoryReactive
) : AuthService {
    override fun register(
        body: RegisterRequest,
        exchange: ServerWebExchange
    ): Mono<UserDTO> {
        return userRepositoryReactive
            .save(
                UserEntity(
                    name = body.name,
                    email = body.email,
                    password = encoder.encode(body.password),
                    role = UserRole.USER,
                    nativeLanguage = body.nativeLanguage
                )
            )
            .flatMap { createUserSession(it) }
            .map { createAuthTokenCookie(it, exchange) }
            .doOnError { cause ->
                if (cause is DataIntegrityViolationException) {
                    throw BadRequestException("User already exists")
                } else {
                    throw cause
                }
            }
    }

    override fun login(
        body: LoginRequest,
        exchange: ServerWebExchange
    ): Mono<UserDTO> {
        return userRepositoryReactive
            .findByEmail(body.email)
            .filter { encoder.matches(body.password, it?.password) }
            .switchIfEmpty(Mono.error(NotFoundException("Invalid email or password")))
            .flatMap { createUserSession(it!!) }
            .map { createAuthTokenCookie(it, exchange) }

    }

    override fun logout(
        exchange: ServerWebExchange
    ): Mono<Void> {
        val tokenFromCookie = exchange.getCookieValue(
            name = jwtProperties.authCookieName
        )

        if (tokenFromCookie == null) {
            return Mono.error(UnauthorizedException("Missing auth token"))
        }

        exchange.invalidateAuthTokenCookie(jwtProperties.authCookieName)

        val claims: Claims = jwtService.parseAllowExpired(tokenFromCookie)

        val jti = claims.id
            ?: return Mono.error(UnauthorizedException("Invalid auth token"))

        return sessionRepositoryReactive
            .deleteByToken(tokenFromCookie)
    }


    private fun createUserSession(user: UserEntity): Mono<Pair<UserDTO, String>> {
        val token = jwtService.createToken(
            jti = UUID.randomUUID().toString(),
            subject = user.email,
        )

        return sessionRepositoryReactive
            .save(
                UserSessionEntity(
                    user = user,
                    token = token
                )
            )
            .thenReturn(Pair(user.toDTO(), token))
    }


    private fun createAuthTokenCookie(
        payload: Pair<UserDTO, String>,
        exchange: ServerWebExchange
    ): UserDTO {
        val (user, token) = payload

        exchange.addAuthTokenCookie(
            name = jwtProperties.authCookieName,
            value = token
        )

        return user
    }


    private fun UserEntity.toDTO(): UserDTO {
        return userMapper.toDTO(this)
    }
}