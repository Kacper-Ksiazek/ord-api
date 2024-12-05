package com.backend.ord.controllers.utils_for_testing

import com.backend.ord.domain.dto.UserDTO
import jakarta.servlet.http.Cookie

data class MockedAuthenticatedUser(
    var token: String,
    var email: String,
    var userInfo: UserDTO,
    var authCookie: Cookie
)
