package com.ord.core.security

import com.ord.config.properties.JwtProperties
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtSecurityContextRepository(
    private val authManager: JwtReactiveAuthenticationManager,
    private val jwtProperties: JwtProperties
) : ServerSecurityContextRepository {
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext?>? {
        val token = exchange.getCookieValue(jwtProperties.authCookieName) ?: return Mono.empty()

        val preAuth = UsernamePasswordAuthenticationToken(null, token)

        return authManager
            .authenticate(preAuth)
            .cache()
            .doOnNext { auth ->
                val details = auth.details

                if (details is Map<*, *> && details.containsKey("renewedToken")) {
                    val renewedToken = details["renewedToken"] as? String

                    if (!renewedToken.isNullOrBlank()) {
                        exchange.addAuthTokenCookie(
                            name = jwtProperties.authCookieName,
                            value = renewedToken
                        )
                    }
                }
            }
            .map<SecurityContext?> { SecurityContextImpl(it) }
            .onErrorResume { error ->
                if (error is MissingUserSessionException) {
                    exchange.invalidateAuthTokenCookie(jwtProperties.authCookieName)
                    // Return empty to trigger 401 Unauthorized
                    Mono.empty()
                } else {
                    Mono.error(error)
                }
            }
    }

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void?>? = Mono.empty()
}