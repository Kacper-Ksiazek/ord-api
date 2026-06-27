package com.ord.features.game.api.controllers

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.game.variants.crossword.api.CrosswordGameFacade
import com.ord.features.game.variants.crossword.dto.api_requests.FinishCrosswordGameRequest
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/games/crossword")
@Tag(
    name = "4. Games: Crossword",
    description = "Crossword puzzle game variant for vocabulary practice"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class CrosswordGameController(
    private val crosswordGameFacade: CrosswordGameFacade
) {
    @PostMapping("/start")
    @Operation(
        summary = "Start a crossword game",
        description = "Initiate a new crossword puzzle game session with selected difficulty and language"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Crossword game started successfully"
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid request data or insufficient words for game",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun startCrosswordGame(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: StartGameRequest,
    ): Mono<ResponseEntity<StartedCrosswordGameResponse>> = crosswordGameFacade.startGame(user.id, body)


    @PostMapping("/finish")
    @Operation(
        summary = "Finish a crossword game",
        description = "Submit answers and complete a crossword game session to receive results and statistics"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Crossword game finished successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Game not found",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun finishCrosswordGame(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: FinishCrosswordGameRequest
    ): Mono<ResponseEntity<FinishedCrosswordGameResponse>> = crosswordGameFacade.finishGame(user.id, body)
}