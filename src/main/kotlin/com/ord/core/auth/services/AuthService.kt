package com.ord.core.auth.services

import com.ord.core.user.model.UserDTO
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

interface AuthService {
    fun requestOtp(email: String): Mono<Void>

    fun verifyOtp(
        email: String,
        code: String,
        exchange: ServerWebExchange
    ): Mono<UserDTO>

    fun logout(
        exchange: ServerWebExchange
    ): Mono<Void>
}