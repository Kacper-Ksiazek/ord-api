package com.ord.core.auth.api

import com.ord.core.auth.api.facade.AuthFacade
import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authFacade: AuthFacade,
) {
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: RegisterRequest,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>> = authFacade.register(body, exchange)


    @PostMapping("/login")
    fun login(
        @Valid @RequestBody body: LoginRequest,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>> = authFacade.login(body, exchange)


    @DeleteMapping("/logout")
    fun logout(
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Void>> = authFacade.logout(exchange)
}