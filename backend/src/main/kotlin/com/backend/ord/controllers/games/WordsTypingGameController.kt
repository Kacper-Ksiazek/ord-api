package com.backend.ord.controllers.games

import com.backend.ord.api.requests.games.StartGameRequest
import com.backend.ord.api.requests.games.WordsTypingToFinishRequest
import com.backend.ord.api.responses.games.FinishedWordsTypingGameResponse
import com.backend.ord.api.responses.games.bases.StartedWordsTypingGameResponse
import com.backend.ord.api.responses.games.utils.computeFinalScore
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.persistence.dto.OngoingWordsTypingGameDTO
import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.shared.controllers.GameControllerBase
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

        val savedGame: OngoingGame = ongoingGameService.save(
            OngoingGame(
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
        @Valid @RequestBody body: WordsTypingToFinishRequest
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

        gameService.completeGame(
            game = game,
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
