package com.ord.core.auth.services.impl

import com.ord.config.properties.JwtProperties
import com.ord.core.auth.models.UserSessionEntity
import com.ord.core.auth.services.AuthService
import com.ord.core.auth.services.EmailService
import com.ord.core.auth.services.OtpService
import com.ord.core.security.JwtService
import com.ord.core.security.UserRepository
import com.ord.core.security.UserSessionRepositoryReactive
import com.ord.core.security.addAuthTokenCookie
import com.ord.core.security.getCookieValue
import com.ord.core.security.invalidateAuthTokenCookie
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.toDTO
import com.ord.exceptions.REST.UnauthorizedException
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class AuthServiceImpl(
    private val jwtProperties: JwtProperties,
    private val jwtService: JwtService,
    private val otpService: OtpService,
    private val emailService: EmailService,

    private val userRepository: UserRepository,
    private val sessionRepositoryReactive: UserSessionRepositoryReactive
) : AuthService {
    override fun requestOtp(email: String): Mono<Void> {
        return otpService
            .generateAndSaveOtp(email)
            .flatMap { otpCode ->
                emailService.sendOtpEmail(email, otpCode)
            }
    }

    override fun verifyOtp(
        email: String,
        code: String,
        exchange: ServerWebExchange
    ): Mono<UserDTO> {
        return otpService
            .verifyAndDeleteOtp(email, code)
            .flatMap { verifiedEmail ->
                userRepository
                    .findByEmail(verifiedEmail)
                    .flatMap { user ->
                        // User exists, return it
                        Mono.just(user!!)
                    }
                    .switchIfEmpty(
                        // User doesn't exist, create new one
                        userRepository.save(
                            com.ord.core.user.model.UserEntity(
                                name = "",
                                email = verifiedEmail,
                                nativeLanguage = null,
                                selectedLearningLanguage = null,
                                isAccountInitialized = false
                            )
                        )
                    )
            }
            .map { it.toDTO() }
            .flatMap { createUserSession(it) }
            .map { createAuthTokenCookie(it, exchange) }
    }

    override fun logout(
        exchange: ServerWebExchange
    ): Mono<Void> {
        val tokenFromCookie = exchange.getCookieValue(jwtProperties.authCookieName)

        if (tokenFromCookie == null) {
            return Mono.error(UnauthorizedException("Missing auth token"))
        }

        exchange.invalidateAuthTokenCookie(jwtProperties.authCookieName)

        return sessionRepositoryReactive
            .deleteByToken(tokenFromCookie)
    }


    private fun createUserSession(user: UserDTO): Mono<Pair<UserDTO, String>> {
        val token = jwtService.createToken(
            jti = UUID.randomUUID().toString(),
            subject = user.email,
        )

        return sessionRepositoryReactive
            .save(
                UserSessionEntity(
                    userId = user.id,
                    token = token
                )
            )
            .thenReturn(Pair(user, token))
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
}