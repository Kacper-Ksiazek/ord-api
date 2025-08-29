package com.ord.core.auth.api.facade

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.auth.services.AuthService
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.UserMapper
import com.ord.shared.api.MonoUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthFacadeImpl(
    private val userMapper: UserMapper,
    private val authService: AuthService
) : AuthFacade {
    override fun register(
        body: RegisterRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<UserDTO>> {
        return MonoUtils.fromBlocking {
            val user = authService.register(body, response)

            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user.toDTO())
        }
    }

    override fun login(
        body: LoginRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<UserDTO>> {
        return MonoUtils.fromBlocking {
            val user = authService.login(body, response).toDTO()

            ResponseEntity
                .status(HttpStatus.OK)
                .body(user)
        }
    }

    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Mono<ResponseEntity<Void>> {
        return MonoUtils.fromBlocking {
            authService.logout(request, response)

            ResponseEntity
                .status(HttpStatus.OK)
                .build()
        }
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