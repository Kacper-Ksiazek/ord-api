package com.ord.features.bank.api

import com.ord.config.OpenApiSecurity
import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.bank.api.responses.BankListItem
import com.ord.features.bank.service.BankService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/banks")
@Tag(
    name = "5. Banks: Management",
    description = "List and manage vocabulary banks for organizing words",
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class BankController(
    private val bankService: BankService,
) {
    @GetMapping
    @Operation(
        summary = "List banks",
        description = "Returns all vocabulary banks owned by the authenticated user.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Banks retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = BankListItem::class))],
            ),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()]),
        ],
    )
    fun listBanks(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ): Mono<ResponseEntity<List<BankListItem>>> =
        bankService
            .findAllListItems(user.id)
            .collectList()
            .map { ResponseEntity.ok(it) }
}
