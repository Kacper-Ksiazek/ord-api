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
    private val userRepositoryReactive: UserRepositoryReactive,
    private val jwtProperties: JwtProperties
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val token = (authentication?.credentials as? String)?.takeIf { it.isNotBlank() } ?: return Mono.empty()

        return Mono.defer {
            Mono.fromCallable { jwtService.parseAndValidate(token).body }
        }.flatMap { claims: Claims ->
            val jti = claims.extractJti()

            sessionsRepository
                .findByToken(jti)
                .switchIfEmpty(Mono.error(BadCredentialsException("Invalid token")))
                .flatMap { session ->
                    userRepositoryReactive
                        .findByEmail(claims.extractSubject())
                        .switchIfEmpty(Mono.error(BadCredentialsException("Invalid token")))
                        .map { user ->
                            authenticatedToken(user!!, null)
                        }

                }
                .onErrorResume { cause ->
                    if (cause is ExpiredJwtException || cause.cause is ExpiredJwtException) {
                        val claims = jwtService.parseAllowExpired(token)
                        val jti = claims.extractJti()

                        sessionsRepository
                            .findByToken(jti)
                            .flatMap { session ->
                                val subject = claims.extractSubject()

                                val newToken = jwtService.createToken(subject = subject)

                                val updatedSession = session!!.copy(
                                    token = newToken,
                                )

                                sessionsRepository
                                    .save(updatedSession)
                                    .then(userRepositoryReactive.findByEmail(email = subject))
                                    .map { user ->
                                        authenticatedToken(user!!, newToken)
                                    }
                            }
                            .switchIfEmpty(Mono.error(BadCredentialsException("Expired and session missing")))
                    } else {
                        Mono.error(cause)
                    }
                }

        }
    }

    private fun authenticatedToken(user: UserEntity, renewedToken: String?): Authentication {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        val auth: AbstractAuthenticationToken = UsernamePasswordAuthenticationToken(user, null, authorities)

        if (renewedToken != null) {
            auth.details = mapOf("renewedToken" to renewedToken)
        }

        return auth
    }
}