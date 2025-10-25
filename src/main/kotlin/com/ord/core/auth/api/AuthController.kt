package com.ord.core.auth.api

import com.ord.core.auth.api.facade.AuthFacade
import com.ord.core.auth.api.requests.dto.OtpRequestDto
import com.ord.core.auth.api.requests.dto.OtpVerifyDto
import com.ord.core.user.model.UserDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
    name = "1. Core: Authentication",
    description = "OTP-based authentication endpoints for user login and logout"
)
class AuthController(
    private val authFacade: AuthFacade,
) {
    @PostMapping("/otp-request")
    @Operation(
        summary = "Request OTP code",
        description = "Sends a one-time password (OTP) to the provided email address. The OTP is valid for 10 minutes."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            ApiResponse(responseCode = "400", description = "Invalid email format", content = [Content()]),
            ApiResponse(responseCode = "429", description = "Too many requests", content = [Content()])
        ]
    )
    fun requestOtp(
        @Valid @RequestBody body: OtpRequestDto
    ): Mono<ResponseEntity<Void>> = authFacade.requestOtp(body.email)

    @PostMapping("/otp-verify")
    @Operation(
        summary = "Verify OTP code and login",
        description = "Verifies the OTP code and returns a JWT token in the response cookie (AUTH-TOKEN) along with user details."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OTP verified successfully, user authenticated",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = UserDTO::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid OTP code", content = [Content()]),
            ApiResponse(responseCode = "401", description = "OTP expired or invalid", content = [Content()])
        ]
    )
    fun verifyOtp(
        @Valid @RequestBody body: OtpVerifyDto,
        @Parameter(hidden = true) exchange: ServerWebExchange
    ): Mono<ResponseEntity<UserDTO>> = authFacade.verifyOtp(body.email, body.code, exchange)


    @DeleteMapping("/logout")
    @Operation(
        summary = "Logout user",
        description = "Logs out the authenticated user by clearing the JWT token cookie."
    )
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Logged out successfully"),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun logout(
        @Parameter(hidden = true) exchange: ServerWebExchange
    ): Mono<ResponseEntity<Void>> = authFacade.logout(exchange)
}