package com.ord.core.user.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.api.facades.UsersFacade
import com.ord.core.user.api.requests.InitUserAccountRequest
import com.ord.core.user.model.UserDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @PostMapping("/init-account")
    fun initUserAccount(
        @AuthenticatedUser user: UserDTO,
        @RequestBody body: InitUserAccountRequest
    ) = userFacade.initUserAccount(user.id, body)
}