package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase
import com.backend.ord.enums.UserRole

data class UserDTO(
    var name: String,
    var email: String,
    private var password: String,
    var role: UserRole = UserRole.USER
) : DTOBase()
