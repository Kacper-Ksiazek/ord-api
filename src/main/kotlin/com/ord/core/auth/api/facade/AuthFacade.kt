package com.ord.core.auth.api.facade

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface AuthFacade {
    fun register(
        body: RegisterRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<UserDTO>>

    fun login(
        body: LoginRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<UserDTO>>

    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<Void>>

    fun me(
        user: UserEntity
    ): Mono<ResponseEntity<UserDTO>>
}