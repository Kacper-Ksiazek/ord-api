package com.ord.core.auth.api

import com.ord.core.auth.api.facade.AuthFacade
import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.auth.security.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.ForbiddenException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authFacade: AuthFacade,
) {
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<UserDTO>> = authFacade.register(request, response)


    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<UserDTO>> = authFacade.login(request, response)


    @DeleteMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<Void>> = authFacade.logout(request, response)


    @GetMapping("/me")
    fun me(
        @AuthenticatedUser user: UserEntity,
    ): Mono<ResponseEntity<UserDTO>> = authFacade.me(user)
}