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
        println("[SECURITY] load: Starting authentication with token: ${token.take(20)}...")

        val preAuth = UsernamePasswordAuthenticationToken(null, token)

        return authManager
            .authenticate(preAuth)
            .doOnSubscribe { println("[SECURITY] load: Authentication Mono subscribed") }
            .doOnNext { auth ->
                println("[SECURITY] load: Authentication completed, checking for renewed token")
                val details = auth.details

                if (details is Map<*, *> && details.containsKey("renewedToken")) {
                    val renewedToken = details["renewedToken"] as? String
                    println("[SECURITY] load: Found renewed token in auth details: ${renewedToken?.take(20)}...")

                    if (!renewedToken.isNullOrBlank()) {
                        println("[SECURITY] load: Setting renewed token cookie")
                        exchange.addAuthTokenCookie(
                            name = jwtProperties.authCookieName,
                            value = renewedToken
                        )
                        println("[SECURITY] load: Renewed token cookie set successfully")
                    }
                } else {
                    println("[SECURITY] load: No renewed token found in auth details")
                }
            }
            .cache()
            .map<SecurityContext?> {
                println("[SECURITY] load: Creating SecurityContext")
                SecurityContextImpl(it)
            }
            .doOnSuccess { println("[SECURITY] load: SecurityContext load completed successfully") }
            .onErrorResume { error ->
                println("[SECURITY] load: ERROR during authentication: ${error.javaClass.simpleName} - ${error.message}")
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