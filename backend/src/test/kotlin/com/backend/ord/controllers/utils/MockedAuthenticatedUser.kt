package com.backend.ord.controllers.utils

import com.backend.ord.domain.dto.UserDTO
import jakarta.servlet.http.Cookie

data class MockedAuthenticatedUser(
    var token: String? = null,
    var email: String? = null,
    var userInfo: UserDTO? = null,
    var authCookie: Cookie? = null
) {
}

