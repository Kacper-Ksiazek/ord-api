package com.ord.features.game.api.controllers

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.features.game.variants.crossword.api.CrosswordGameFacade
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.crossword.dto.api_requests.FinishCrosswordGameRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/games/crossword")
class CrosswordGameController(
    private val crosswordGameFacade: CrosswordGameFacade
) {
    /**
     * Start a new crossword game
     */
    @PostMapping("/start")
    fun startCrosswordGame(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: StartGameRequest,
    ): Mono<ResponseEntity<StartedCrosswordGameResponse>> = crosswordGameFacade.startGame(user, body)


    /**
     * Finish a crossword game
     */
    @PostMapping("/finish")
    fun finishCrosswordGame(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: FinishCrosswordGameRequest
    ): Mono<ResponseEntity<FinishedCrosswordGameResponse>> = crosswordGameFacade.finishGame(user, body)
}