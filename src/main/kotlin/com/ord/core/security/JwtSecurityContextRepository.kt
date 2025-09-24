package com.ord.core.security

import com.ord.config.properties.JwtProperties
import org.springframework.http.ResponseCookie
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

        return authManager.authenticate(preAuth)
            .doOnNext { auth ->
                val details = auth.details

                if (details is Map<*, *> && details["renewedToken"] is String) {
                    exchange.attributes[JwtExchangeAttrs.RENEWED_TOKEN_ATTR] = details["renewedToken"] as String
                }
            }
            .map<SecurityContext?> { SecurityContextImpl(it) }
            .onErrorResume { error ->
                if (error is MissingUserSessionException) {
                    clearAuthCookie(exchange)
                    Mono.empty()
                } else {
                    Mono.error(error)
                }
            }
    }

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void?>? = Mono.empty()

    private fun clearAuthCookie(exchange: ServerWebExchange) {
        val expiredCookie = ResponseCookie.from(jwtProperties.authCookieName, "")
            .maxAge(0)
            .path("/")
            .build()

        exchange.response.addCookie(expiredCookie)
    }
}