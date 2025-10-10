package com.ord.core.user.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.api.facades.UsersFacade
import com.ord.core.user.model.UserDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UsersController(
    private val userFacade: UsersFacade,
) {
    @GetMapping("/me")
    fun me(
        @AuthenticatedUser user: UserDTO,
    ) = userFacade.me(user)
}