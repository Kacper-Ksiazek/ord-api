package com.ord.core.auth.api

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.auth.jwt.JwtService
import com.ord.core.auth.services.AuthService
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserMapper
import com.ord.exceptions.ForbiddenException
import com.ord.exceptions.UserNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userMapper: UserMapper,
    private val jwtService: JwtService,
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequest,
        response: HttpServletResponse
    ): ResponseEntity<UserDTO?> {
        try {
            val user = authService.register(request, response)

            // Return HTTP 201 Created with user data
            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(user))
        } catch (e: UserNotFoundException) {
            return ResponseEntity.badRequest().build()
        } catch (e: DataIntegrityViolationException) {
            return ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<UserDTO?> {
        try {
            val user = authService.login(request, response)
            // Return HTTP 200 OK with user data
            return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDTO(user))
        } catch (e: UserNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<Void> {
        try {
            authService.logout(request, response)
            return ResponseEntity.ok().build()
        } //
        catch (e: ForbiddenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @GetMapping("/me")
    fun getCurrentlyLoggedInUser(
        request: HttpServletRequest
    ): ResponseEntity<UserDTO?> {
        val authenticatedUser = jwtService.getAuthenticatedUser(request)

        return if (authenticatedUser != null) {
            ResponseEntity.ok(userMapper.toDTO(authenticatedUser))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}