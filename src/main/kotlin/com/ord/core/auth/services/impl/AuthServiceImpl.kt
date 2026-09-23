package com.ord.core.auth.services.impl

import com.ord.config.properties.OtpProperties
import com.ord.config.properties.SessionProperties
import com.ord.core.auth.models.UserSessionEntity
import com.ord.core.auth.model.enums.UiLocale
import com.ord.core.auth.services.AuthService
import com.ord.core.auth.services.EmailService
import com.ord.core.auth.services.OtpService
import com.ord.core.security.SessionTokenService
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
import java.time.Instant

@Service
class AuthServiceImpl(
    private val sessionProperties: SessionProperties,
    private val otpProperties: OtpProperties,
    private val sessionTokenService: SessionTokenService,
    private val otpService: OtpService,
    private val emailService: EmailService,
    private val userRepository: UserRepository,
    private val sessionRepositoryReactive: UserSessionRepositoryReactive,
) : AuthService {

    override fun requestOtp(email: String, locale: UiLocale?): Mono<Void> {
        val resolvedLocale = UiLocale.resolve(locale)
        return otpService
            .generateAndSaveOtp(email)
            .flatMap { otpCode ->
                if (otpProperties.isEmailWhitelisted(email)) {
                    Mono.empty()
                } else {
                    emailService.sendOtpEmail(email, otpCode, resolvedLocale)
                }
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
                        Mono.just(user)
                    }
                    .switchIfEmpty(
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
        val tokenFromCookie = exchange.getCookieValue(sessionProperties.cookieName)

        if (tokenFromCookie == null) {
            return Mono.error(UnauthorizedException("Missing auth token"))
        }

        exchange.invalidateAuthTokenCookie(
            name = sessionProperties.cookieName,
            secure = sessionProperties.cookieSecure,
            sameSite = sessionProperties.cookieSameSite,
        )

        return sessionRepositoryReactive
            .deleteByTokenHash(sessionTokenService.hash(tokenFromCookie))
    }


    private fun createUserSession(user: UserDTO): Mono<Pair<UserDTO, String>> {
        val rawToken = sessionTokenService.generateRawToken()
        val now = Instant.now()

        return sessionRepositoryReactive
            .save(
                UserSessionEntity(
                    tokenHash = sessionTokenService.hash(rawToken),
                    userId = user.id,
                    createdAt = now,
                    lastSeenAt = now,
                    idleExpiresAt = now.plus(sessionProperties.idleTimeout),
                    absoluteExpiresAt = now.plus(sessionProperties.absoluteTimeout),
                )
            )
            .thenReturn(Pair(user, rawToken))
    }


    private fun createAuthTokenCookie(
        payload: Pair<UserDTO, String>,
        exchange: ServerWebExchange
    ): UserDTO {
        val (user, token) = payload

        exchange.addAuthTokenCookie(
            name = sessionProperties.cookieName,
            value = token,
            secure = sessionProperties.cookieSecure,
            sameSite = sessionProperties.cookieSameSite,
        )

        return user
    }
}
