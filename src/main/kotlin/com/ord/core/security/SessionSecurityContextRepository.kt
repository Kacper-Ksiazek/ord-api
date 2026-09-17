package com.ord.core.security

import com.ord.config.properties.SessionProperties
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SessionSecurityContextRepository(
    private val authManager: SessionReactiveAuthenticationManager,
    private val sessionProperties: SessionProperties,
) : ServerSecurityContextRepository {
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val token = exchange.getCookieValue(sessionProperties.cookieName) ?: return Mono.empty()

        val preAuth = UsernamePasswordAuthenticationToken(null, token)

        return authManager
            .authenticate(preAuth)
            .cache()
            .map<SecurityContext> { SecurityContextImpl(it) }
            .onErrorResume { error ->
                if (error is MissingUserSessionException) {
                    exchange.invalidateAuthTokenCookie(
                        name = sessionProperties.cookieName,
                        secure = sessionProperties.cookieSecure,
                        sameSite = sessionProperties.cookieSameSite,
                    )
                    Mono.empty()
                } else {
                    Mono.error(error)
                }
            }
    }

    override fun save(exchange: ServerWebExchange, context: SecurityContext?): Mono<Void> = Mono.empty()
}
