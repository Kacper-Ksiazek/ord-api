package com.ord.features.game.api.controllers

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.features.game.services.OngoingGameService
import com.ord.features.game.variants.shared.dto.api_requests.CancelGameRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/games")
@Tag(
    name = "4. Games: General",
    description = "General game management endpoints for all game types"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class GamesController(
    private val ongoingGameService: OngoingGameService,
) {
    // +-----------------------------+
    // |    GAMES ENDPOINTS PLAN:    |
    // +-----------------------------+

    // Endpoint to return all available games with their difficulties for the user in a given language. It should retrieve the number of words they have in the DB and then
    // return the information which games can be played with the number of words they have.

    // ----
    // 1. Start a game endpoints
    //
    // 1. @PostMapping("/start/words-typing")
    // 2. @PostMapping("/start/gaps-filling")
    // 3. @PostMapping("/start/sentences-writing")
    //
    // 4. @PostMapping("/start/immersive-story")

    // ----
    // 2. Review a game endpoints
    // ----
    // 5. [✅] @PostMapping("/finish/crossword")
    // 6. @PostMapping("/finish/words-typing")
    // 7. @PostMapping("/finish/gaps-filling")
    // 8. @PostMapping("/finish/sentences-writing")
    //
    // 9. @PostMapping("/finish/immersive-story")

    // ----
    // 3. General game endpoints ( ALL MVP )
    // ----
    // 10. @PostMapping("/pause/{gameId}")
    // 11. @PostMapping("/resume/{gameId}")
    // 12. [MVP] @PostMapping("/cancel/{gameId}")
    // 13. @GetMapping("/paused")
    // 14. @GetMapping("/games-history")
    // 15. @GetMapping("/statistics")

    @PostMapping("/cancel/{gameId}")
    @Operation(
        summary = "Cancel an ongoing game",
        description = "Cancel an active game session before completion"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "Game cancelled successfully",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Game not found",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun cancelGame(
        @Parameter(
            description = "Ongoing game ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable gameId: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CancelGameRequest
    ): Mono<ResponseEntity<Unit>> {
        return ongoingGameService
            .cancelGame(
                ongoingGameId = gameId,
                userId = user.id,
                duration = body.duration
            )
            .then(Mono.fromCallable {
                ResponseEntity.noContent().build<Unit>()
            })
    }
}