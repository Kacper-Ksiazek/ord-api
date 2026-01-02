package com.ord.core.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.exceptions.dto.api_responses.HTTPErrorResponse
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ApiAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : ServerAuthenticationEntryPoint {

    override fun commence(
        exchange: ServerWebExchange,
        ex: AuthenticationException
    ): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON

        val errorResponse = HTTPErrorResponse(
            message = "Authentication required",
            status = HttpStatus.UNAUTHORIZED.value()
        )

        val buffer: DataBuffer = exchange.response.bufferFactory()
            .wrap(objectMapper.writeValueAsBytes(errorResponse))

        return exchange.response.writeWith(Mono.just(buffer))
    }
}
