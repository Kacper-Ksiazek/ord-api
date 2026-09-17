package com.ord.config.security

import com.ord.exceptions.dto.api_responses.HTTPErrorResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import tools.jackson.databind.json.JsonMapper

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class CsrfOriginWebFilter(
    @Value("\${cors.allowed-origins}") allowedOrigins: String,
    private val objectMapper: JsonMapper,
) : WebFilter {
    private val allowedOrigins = allowedOrigins
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() && it != "*" }
        .toSet()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val method = exchange.request.method
        if (method == null || method !in UNSAFE_METHODS) {
            return chain.filter(exchange)
        }

        val origin = exchange.request.headers.origin
        if (origin != null && origin in allowedOrigins) {
            return chain.filter(exchange)
        }

        return reject(exchange)
    }

    private fun reject(exchange: ServerWebExchange): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.FORBIDDEN
        response.headers.contentType = MediaType.APPLICATION_JSON

        val errorResponse = HTTPErrorResponse(
            message = "Origin is not allowed",
            status = HttpStatus.FORBIDDEN.value(),
        )
        val buffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(errorResponse))
        return response.writeWith(Mono.just(buffer))
    }

    companion object {
        private val UNSAFE_METHODS = setOf(
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.PATCH,
            HttpMethod.DELETE,
        )
    }
}
