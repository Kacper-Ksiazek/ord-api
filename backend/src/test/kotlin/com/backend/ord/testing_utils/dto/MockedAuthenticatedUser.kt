package com.backend.ord.testing_utils.dto

import com.backend.ord.domain.persistence.dto.UserDTO
import jakarta.servlet.http.Cookie

data class MockedAuthenticatedUser(
    var token: String,
    var email: String,
    var userInfo: UserDTO,
    var authCookie: Cookie
)