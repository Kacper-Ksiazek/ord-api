package com.ord.core.auth.api.facade

import com.ord.core.auth.services.AuthService
import com.ord.core.user.model.UserDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Service
class AuthFacadeImpl(
    private val authService: AuthService
) : AuthFacade {
    override fun requestOtp(email: String): Mono<ResponseEntity<Void>> {
        return authService
            .requestOtp(email)
            .thenReturn(
                ResponseEntity
                    .status(HttpStatus.OK)
                    .build()
            )
    }

    override fun verifyOtp(
        email: String,
        code: String,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>> {
        return authService
            .verifyOtp(email, code, exchange)
            .map { user ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(user)
            }
    }

    override fun logout(
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Void>> {
        return authService
            .logout(exchange)
            .then(Mono.fromCallable {
                ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build()
            })
    }
}