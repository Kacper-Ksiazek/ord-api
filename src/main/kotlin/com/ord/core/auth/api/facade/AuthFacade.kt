package com.ord.core.auth.api.facade

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

interface AuthFacade {
    fun register(
        body: RegisterRequest,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>>

    fun login(
        body: LoginRequest,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>>

    fun logout(
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Void>>

    fun me(
        user: UserEntity
    ): Mono<ResponseEntity<UserDTO>>
}