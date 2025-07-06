package com.ord.features.game.api.controllers

import com.ord.config.GamesConfig
import com.ord.core.auth.security.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.variants.crossword.api.CrosswordGameFacade
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.shared.api.GameControllerBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableProperAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.ProperAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.computeFinalScore
import com.ord.features.game.variants.shared.enums.AnswerScore
import com.ord.features.game.variants.words_typing.dto.api_requests.FinishCrosswordGameRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    ): ResponseEntity<StartedCrosswordGameResponse> = crosswordGameFacade.startGame(user, body)


    /**
     * Finish a crossword game
     */
    @PostMapping("/finish")
    fun finishCrosswordGame(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: FinishCrosswordGameRequest
    ): ResponseEntity<FinishedCrosswordGameResponse> = crosswordGameFacade.finishGame(user, body)
}