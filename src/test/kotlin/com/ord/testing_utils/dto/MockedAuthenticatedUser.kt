package com.ord.testing_utils.dto

import com.ord.core.user.model.UserDTO
import jakarta.servlet.http.Cookie

data class MockedAuthenticatedUser(
    var token: String,
    var email: String,
    var userInfo: UserDTO,
    var authCookie: Cookie
)