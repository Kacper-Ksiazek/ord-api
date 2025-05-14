package com.backend.ord.services

import com.backend.ord.api.requests.LoginRequest
import com.backend.ord.api.requests.RegisterRequest
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.exceptions.ForbiddenException
import com.backend.ord.exceptions.UserNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface AuthenticationService {
    @Throws(UserNotFoundException::class)
    fun register(
        request: RegisterRequest,
        response: HttpServletResponse
    ): UserEntity

    @Throws(UserNotFoundException::class)
    fun login(
        request: LoginRequest,
        response: HttpServletResponse
    ): UserEntity

    @Throws(ForbiddenException::class)
    fun logout(request: HttpServletRequest, response: HttpServletResponse)
}
