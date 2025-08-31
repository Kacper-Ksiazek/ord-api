package com.ord.core.auth.api.facade

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.auth.services.AuthService
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.UserMapper
import com.ord.shared.api.MonoUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Service
class AuthFacadeImpl(
    private val userMapper: UserMapper,
    private val authService: AuthService
) : AuthFacade {
    override fun register(
        body: RegisterRequest,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>> {
        return authService
            .register(body, exchange)
            .map { user ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(user)
            }
    }

    override fun login(
        body: LoginRequest,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>> {
        return authService.login(body, exchange).map { user ->
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

    override fun me(user: UserEntity): Mono<ResponseEntity<UserDTO>> {
        return MonoUtils.fromBlocking {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(user.toDTO())
        }
    }

    private fun UserEntity.toDTO(): UserDTO {
        return userMapper.toDTO(this)
    }
}