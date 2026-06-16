package com.ord.core.user.api

import com.ord.config.OpenApiSecurity
import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.api.facades.UsersFacade
import com.ord.core.user.api.requests.InitUserAccountRequest
import com.ord.core.user.model.UserDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
@Tag(
    name = "1. Core: Users",
    description = "User profile management and account initialization"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class UsersController(
    private val userFacade: UsersFacade,
) {
    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Returns the profile information of the currently authenticated user."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User profile retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = UserDTO::class))]
            ),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun me(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ) = userFacade.me(user)

    @PostMapping("/init-account")
    @Operation(
        summary = "Initialize user account",
        description = "Sets up the user's account with initial language proficiencies. This is typically done after first login."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Account initialized successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = UserDTO::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid request data", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun initUserAccount(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @RequestBody body: InitUserAccountRequest
    ) = userFacade.initUserAccount(user.id, body)
}