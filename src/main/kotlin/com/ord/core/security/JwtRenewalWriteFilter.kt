package com.ord.core.security

import com.ord.config.properties.JwtProperties
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtRenewalWriteFilter(
    private val jwtProperties: JwtProperties,
) : WebFilter {
    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain
    ): Mono<Void> {
        return chain
            .filter(exchange)
            .doOnSuccess {
                val renewedToken = exchange.attributes[JwtExchangeAttrs.RENEWED_TOKEN_ATTR] as? String

                if (!renewedToken.isNullOrEmpty()) {
                    exchange.addAuthTokenCookie(
                        name = jwtProperties.authCookieName,
                        value = renewedToken
                    )
                }
            }
    }
}