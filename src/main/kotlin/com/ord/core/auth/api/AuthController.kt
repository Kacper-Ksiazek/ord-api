package com.ord.core.auth.api

import com.ord.core.auth.api.facade.AuthFacade
import com.ord.core.auth.api.requests.dto.OtpRequestDto
import com.ord.core.auth.api.requests.dto.OtpVerifyDto
import com.ord.core.user.model.UserDTO
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
    @PostMapping("/otp-request")
    fun requestOtp(
        @Valid @RequestBody body: OtpRequestDto
    ): Mono<ResponseEntity<Void>> = authFacade.requestOtp(body.email)

    @PostMapping("/otp-verify")
    fun verifyOtp(
        @Valid @RequestBody body: OtpVerifyDto,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>> = authFacade.verifyOtp(body.email, body.code, exchange)


    @DeleteMapping("/logout")
    fun logout(
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Void>> = authFacade.logout(exchange)
}