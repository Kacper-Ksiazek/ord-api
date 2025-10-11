package com.ord.core.auth.api.facade

import com.ord.core.user.model.UserDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

interface AuthFacade {
    fun requestOtp(email: String): Mono<ResponseEntity<Void>>

    fun verifyOtp(
        email: String,
        code: String,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>>

    fun logout(
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Void>>
}