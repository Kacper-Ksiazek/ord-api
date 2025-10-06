package com.ord.core.auth.services

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.user.model.UserDTO
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

interface AuthService {
    fun register(
        body: RegisterRequest,
        exchange: ServerWebExchange
    ): Mono<UserDTO>


    fun login(
        body: LoginRequest,
        exchange: ServerWebExchange
    ): Mono<UserDTO>


    fun logout(
        exchange: ServerWebExchange
    ): Mono<Void>
}