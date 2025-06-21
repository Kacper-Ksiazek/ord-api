package com.ord.core.auth.services

import com.ord.core.auth.api.requests.dto.LoginRequest
import com.ord.core.auth.api.requests.dto.RegisterRequest
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.ForbiddenException
import com.ord.exceptions.UserNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface AuthService {
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