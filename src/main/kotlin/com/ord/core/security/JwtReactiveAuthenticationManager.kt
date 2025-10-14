package com.ord.core.security

import com.ord.config.properties.JwtProperties
import com.ord.core.user.model.UserEntity
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtReactiveAuthenticationManager(
    private val jwtService: JwtService,
    private val sessionsRepository: UserSessionRepositoryReactive,
    private val userRepository: UserRepository,
    private val jwtProperties: JwtProperties
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val token = (authentication?.credentials as? String)?.takeIf { it.isNotBlank() } ?: return Mono.empty()
        println("[AUTH] authenticate: Starting authentication for token: ${token.take(20)}...")

        return Mono
            .defer {
                println("[AUTH] authenticate: Calling parseAndValidate")
                Mono.fromCallable { jwtService.parseAndValidate(token).body }
            }
            .doOnNext { claims ->
                println("[AUTH] authenticate: Token validation successful, not expired")
            }
            .flatMap { claims: Claims ->
                authenticateWithValidToken(token, claims)
            }
            .onErrorResume { cause ->
                println("[AUTH] authenticate: Error during validation: ${cause.javaClass.simpleName} - ${cause.message}")
                if (cause is ExpiredJwtException || cause.cause is ExpiredJwtException) {
                    println("[AUTH] authenticate: Token is expired, calling handleExpiredToken")
                    handleExpiredToken(token)
                } else {
                    println("[AUTH] authenticate: Non-expiration error, re-throwing")
                    Mono.error(cause)
                }
            }
    }

    private fun authenticateWithValidToken(token: String, claims: Claims): Mono<Authentication> {
        return sessionsRepository
            .findByToken(token)
            .switchIfEmpty(Mono.error(MissingUserSessionException("Invalid token - no corresponding session found")))
            .flatMap { session ->
                userRepository
                    .findByEmail(claims.extractSubject())
                    .switchIfEmpty(Mono.error(MissingUserSessionException("Invalid token - no corresponding user found")))
                    .map { user ->
                        authenticatedToken(user!!, null)
                    }
            }
    }

    private fun handleExpiredToken(token: String): Mono<Authentication> {
        println("[AUTH] handleExpiredToken: Starting token refresh for expired token: ${token.take(20)}...")
        return Mono
            .fromCallable {
                jwtService.parseAllowExpired(token)
            }
            .doOnNext { println("[AUTH] handleExpiredToken: Successfully parsed expired token") }
            .flatMap { claims ->
                println("[AUTH] handleExpiredToken: Looking up session by token")
                sessionsRepository
                    .findByToken(token)
                    .doOnNext { session ->
                        println("[AUTH] handleExpiredToken: Found session with ID: ${session?.id}")
                    }
                    .switchIfEmpty(
                        Mono.defer {
                            println("[AUTH] handleExpiredToken: ERROR - No session found for expired token")
                            Mono.error(MissingUserSessionException("Expired token - no corresponding session found"))
                        }
                    )
                    .flatMap { session ->
                        val subject = claims.extractSubject()
                        val newToken = jwtService.createToken(subject = subject)
                        println("[AUTH] handleExpiredToken: Generated new token for subject: $subject, new token: ${newToken.take(20)}...")

                        val updatedSession = session!!.copy(
                            token = newToken,
                        )

                        println("[AUTH] handleExpiredToken: About to save updated session with ID: ${session.id}")
                        userRepository
                            .findByEmail(email = subject)
                            .doOnNext { user ->
                                println("[AUTH] handleExpiredToken: Found user: ${user?.email}")
                            }
                            .delayUntil {
                                println("[AUTH] handleExpiredToken: Executing session save...")
                                sessionsRepository.save(updatedSession)
                                    .doOnSuccess { savedSession ->
                                        println("[AUTH] handleExpiredToken: Session saved successfully! ID: ${savedSession?.id}, token: ${savedSession?.token?.take(20)}...")
                                    }
                                    .doOnError { error ->
                                        println("[AUTH] handleExpiredToken: ERROR saving session: ${error.message}")
                                    }
                            }
                            .map { user ->
                                println("[AUTH] handleExpiredToken: Creating authenticated token with renewed token")
                                authenticatedToken(user!!, newToken)
                            }
                            .doOnSuccess {
                                println("[AUTH] handleExpiredToken: Token refresh completed successfully")
                            }
                    }
            }
    }

    private fun authenticatedToken(user: UserEntity, renewedToken: String?): Authentication {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        val auth = UsernamePasswordAuthenticationToken(user, null, authorities)

        if (renewedToken != null) {
            auth.details = mapOf("renewedToken" to renewedToken)
        }

        return auth
    }
}