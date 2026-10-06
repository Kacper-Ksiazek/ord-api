package com.ord.features.home.api

import com.ord.config.OpenApiSecurity
import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.home.api.facades.HomeFacade
import com.ord.features.home.model.HomeActivityPerDay
import com.ord.features.home.model.HomeResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/home")
@Tag(
    name = "6. Home",
    description = "Landing page reads: vocabulary, conversations, and finished games, plus this year's activity",
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class HomeController(
    private val homeFacade: HomeFacade,
) {
    @GetMapping
    @Operation(
        summary = "Get home summary",
        description = "Count cards, sparklines, and recent items for the authenticated user and their selected learning language. When no language is selected, totals are zero.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Home summary retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = HomeResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()],
            ),
        ],
    )
    fun getHome(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ) = homeFacade.getHome(user)

    @GetMapping("/activity")
    @Operation(
        summary = "Get home activity",
        description = "Sparse day counts for the current UTC calendar year. When no language is selected, the year has no days.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Home activity retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = HomeActivityPerDay::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()],
            ),
        ],
    )
    fun getActivity(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ) = homeFacade.getActivity(user)
}
