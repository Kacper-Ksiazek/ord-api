package com.ord.features.game.api.controllers

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.features.game.variants.words_typing.dto.api_requests.FinishWordsTypingGameRequest
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.words_typing.api.WordsTypingGameFacade
import com.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
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
@RequestMapping("/api/v1/games/words-typing")
@Tag(
    name = "4. Games: Words Typing",
    description = "Fast-paced word typing game to improve vocabulary recall and typing speed"
)
@SecurityRequirement(name = "bearer-jwt")
class WordsTypingGameController(
    private val wordsTypingGameFacade: WordsTypingGameFacade
) {
    @PostMapping("/start")
    @Operation(
        summary = "Start a words typing game",
        description = "Initiate a new word typing game session to practice quick recall and accurate typing"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Words typing game started successfully"
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
    fun startWordsTypingGame(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: StartGameRequest
    ): Mono<ResponseEntity<StartedWordsTypingGameResponse>> = wordsTypingGameFacade.startGame(user.id, body = body)

    @PostMapping("/finish")
    @Operation(
        summary = "Finish a words typing game",
        description = "Submit typed words and complete the game session to receive results including speed and accuracy metrics"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Words typing game finished successfully"
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
    fun finishWordsTypingGame(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: FinishWordsTypingGameRequest
    ): Mono<ResponseEntity<FinishedWordsTypingGameResponse>> = wordsTypingGameFacade.finishGame(user.id, body)
}