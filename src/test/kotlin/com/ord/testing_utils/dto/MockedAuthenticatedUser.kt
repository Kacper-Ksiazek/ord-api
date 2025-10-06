package com.ord.testing_utils.dto

import com.ord.core.user.model.UserDTO
import org.springframework.http.ResponseCookie

data class MockedAuthenticatedUser(
    var token: String,
    var email: String,
    var userInfo: UserDTO,
    var authCookie: ResponseCookie
)
