package com.backend.ord.features.game.api.controllers

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.backend.ord.features.game.model.ongoing_game.OngoingWordsTypingGameDTO
import com.backend.ord.features.game.model.ongoing_game.enums.GameType
import com.backend.ord.features.game.variants.crossword.dto.api_requests.FinishWordsTypingGameRequest
import com.backend.ord.features.game.variants.shared.controllers.GameControllerBase
import com.backend.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.backend.ord.features.game.variants.shared.dto.api_responses.helpers.computeFinalScore
import com.backend.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.backend.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games/words-typing")
class WordsTypingGameController : GameControllerBase() {

    @PostMapping("/start")
    fun startWordsTypingGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: StartGameRequest
    ): ResponseEntity<StartedWordsTypingGameResponse> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val (instruction, properAnswers) = aiGameService.generateWordsTypingGame(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        val savedGame: OngoingGameEntity = ongoingGameService.save(
            OngoingGameEntity(
                user = user,
                type = GameType.WORDS_TYPING,

                language = body.language,
                difficulty = body.difficulty,
                properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
            )
        )

        return ResponseEntity.ok(
            StartedWordsTypingGameResponse(
                gameId = savedGame.id,
                instruction = instruction,
                properAnswers = properAnswers
            )
        )
    }

    @PostMapping("/finish")
    fun finishWordsTypingGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: FinishWordsTypingGameRequest
    ): ResponseEntity<FinishedWordsTypingGameResponse> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val game: OngoingWordsTypingGameDTO = ongoingGameMapper.toWordsTypingDTO(
            entity = ongoingGameService.findByIdOrFail(id = body.gameId, userId = user.id)
        )

        val reviewedQuestions = gameReviewService.reviewUserAnswersAndUpdateDBPoints(
            user = user,
            language = game.language,
            difficulty = game.difficulty,
            expectedAnswers = game.properAnswers,
            userAnswers = body.answers
        )

        val totalPoints = reviewedQuestions.computeFinalScore()

        ongoingGameService.completeGame(
            ongoingGame = game,
            totalPoints = totalPoints,
            duration = body.duration
        )

        return ResponseEntity.ok(
            FinishedWordsTypingGameResponse(
                totalPoints = totalPoints,
                properAnswers = reviewedQuestions
            )
        )
    }
}