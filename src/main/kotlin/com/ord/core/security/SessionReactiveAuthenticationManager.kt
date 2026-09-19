package com.ord.core.security

import com.ord.config.properties.SessionProperties
import com.ord.core.auth.models.UserSessionEntity
import com.ord.core.user.model.UserEntity
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant

@Component
class SessionReactiveAuthenticationManager(
    private val sessionTokenService: SessionTokenService,
    private val sessionsRepository: UserSessionRepositoryReactive,
    private val userRepository: UserRepository,
    private val sessionProperties: SessionProperties,
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val rawToken = (authentication.credentials as? String)?.takeIf { it.isNotBlank() } ?: return Mono.empty()
        val tokenHash = sessionTokenService.hash(rawToken)

        return sessionsRepository
            .findByTokenHash(tokenHash)
            .switchIfEmpty(Mono.error(MissingUserSessionException("Invalid token - no corresponding session found")))
            .flatMap { session -> authenticateSession(session) }
    }

    private fun authenticateSession(session: UserSessionEntity): Mono<Authentication> {
        val now = Instant.now()

        if (session.isExpired(now)) {
            val delete = session.id
                ?.let { sessionsRepository.deleteById(it) }
                ?: Mono.empty()

            return delete.then(
                Mono.error(MissingUserSessionException("Session expired"))
            )
        }

        return userRepository
            .findById(session.userId)
            .switchIfEmpty(Mono.error(MissingUserSessionException("Invalid token - no corresponding user found")))
            .delayUntil { slideSession(session = session, now = now) }
            .map { user -> authenticatedToken(user) }
    }

    private fun slideSession(session: UserSessionEntity, now: Instant): Mono<*> {
        if (Duration.between(session.lastSeenAt, now) < sessionProperties.slideInterval) {
            return Mono.empty<Void>()
        }

        val proposedIdleExpiry = now.plus(sessionProperties.idleTimeout)
        val idleExpiresAt = minOf(proposedIdleExpiry, session.absoluteExpiresAt)

        return sessionsRepository.save(
            session.copy(
                lastSeenAt = now,
                idleExpiresAt = idleExpiresAt,
            )
        )
    }

    private fun authenticatedToken(user: UserEntity): Authentication {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        return UsernamePasswordAuthenticationToken(user, null, authorities)
    }
}
