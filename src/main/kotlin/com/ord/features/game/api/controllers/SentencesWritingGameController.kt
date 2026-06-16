package com.ord.features.game.api.controllers

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.variants.sentences_writing.api.SentencesWritingGameFacade
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
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
@RequestMapping("/api/v1/games/sentences-writing")
@Tag(
    name = "4. Games: Sentences Writing",
    description = "Sentence writing game variant to practice vocabulary usage in context"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class SentencesWritingGameController(
    private val sentencesWritingGameFacade: SentencesWritingGameFacade
) {

    @PostMapping("/start")
    @Operation(
        summary = "Start a sentences writing game",
        description = "Initiate a new sentence writing game session where users create sentences using target words"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Sentences writing game started successfully"
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
    ): Mono<ResponseEntity<StartedSentencesWritingGameResponse>> = sentencesWritingGameFacade.startGame(user.id, body)

    @PostMapping("/finish")
    @Operation(
        summary = "Finish a sentences writing game",
        description = "Submit written sentences and complete the game session to receive AI-powered feedback and evaluation"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Sentences writing game finished successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Game not found",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid request data or sentence exceeds 1024 characters",
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
        @Valid @RequestBody body: FinishSentencesWritingGameRequest
    ): Mono<ResponseEntity<FinishedSentencesWritingGameResponse>> {
        body.answers.forEach {
            if (it.value.length > 1024) {
                throw BadRequestException("Each sentence answer must up to 1024 characters, but got ${it.value.length} characters.")
            }
        }

        return sentencesWritingGameFacade.finishGame(user.id, body)
    }
}