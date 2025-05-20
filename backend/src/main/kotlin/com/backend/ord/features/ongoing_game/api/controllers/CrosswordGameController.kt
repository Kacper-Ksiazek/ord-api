package com.backend.ord.features.ongoing_game.api.controllers

import com.backend.ord.config.GamesConfig
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.ongoing_game.model.OngoingCrosswordGameDTO
import com.backend.ord.features.ongoing_game.model.OngoingGame
import com.backend.ord.features.ongoing_game.model.enums.GameType
import com.backend.ord.features.ongoing_game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.backend.ord.features.ongoing_game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_requests.StartGameRequest
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.helpers.IdentifiableProperAnswer
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.helpers.ProperAnswer
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.helpers.computeFinalScore
import com.backend.ord.features.ongoing_game.variants.shared.enums.AnswerScore
import com.backend.ord.features.ongoing_game.variants.words_typing.dto.api_requests.FinishCrosswordGameRequest
import com.backend.ord.services.GameReviewService
import com.backend.ord.shared.controllers.GameControllerBase
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games/crossword")
class CrosswordGameController : GameControllerBase() {
    /**
     * Start a new crossword game
     */
    @PostMapping("/start")
    fun startCrosswordGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: StartGameRequest
    ): ResponseEntity<StartedCrosswordGameResponse> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val (instruction, properAnswers) = aiGameService.generateCrosswordGame(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        val savedGame: OngoingGame = ongoingGameService.save(
            OngoingGame(
                user = user,
                type = GameType.CROSSWORD,

                language = body.language,
                difficulty = body.difficulty,
                properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
            )
        )

        return ResponseEntity.ok(
            StartedCrosswordGameResponse(
                gameId = savedGame.id,
                instruction = instruction,
                properAnswers = properAnswers
            )
        )
    }

    /**
     * Finish a crossword game
     */
    @PostMapping("/finish")
    fun finishCrosswordGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: FinishCrosswordGameRequest
    ): ResponseEntity<FinishedCrosswordGameResponse> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val game: OngoingCrosswordGameDTO = ongoingGameMapper.toCrosswordDTO(
            entity = ongoingGameService.findByIdOrFail(id = body.gameId, userId = user.id)
        )

        // 3. Check all words forming a crossword
        val reviewedQuestions: Set<IdentifiableProperAnswer> = gameReviewService.reviewUserAnswersAndUpdateDBPoints(
            user = user,
            language = game.language,
            difficulty = game.difficulty,
            expectedAnswers = game.properAnswers.questions,
            userAnswers = body.answers.questions,
        )

        // 4. Compute points received from words forming a crossword
        val pointsForQuestions: Int = reviewedQuestions.computeFinalScore(
            moduleRatio = GamesConfig.Points.ScoreFactorsRatio.Crossword.QUESTIONS
        )

        // 5. Compute points received from the final word
        val reviewedFinalAnswer: AnswerScore = AnswerScore.Companion.reviewUserAnswer(
            userAnswer = body.answers.finalWord,
            expectedAnswer = game.properAnswers.finalWord,
            difficulty = game.difficulty
        )

        val pointsForFinalWord: Int = GameReviewService.Companion.computeFinalScoreComponent(
            receivedPoints = reviewedFinalAnswer.wage,
            maxPoints = AnswerScore.CORRECT.wage,
            moduleRatio = GamesConfig.Points.ScoreFactorsRatio.Crossword.FINAL_WORD
        )

        // 6. Add points together and compute the total score
        val totalPoints: Int = pointsForQuestions + pointsForFinalWord

        // 7. Update the game in the database
        gameService.completeGame(
            game = game,
            totalPoints = totalPoints,
            duration = body.duration
        )

        // 8. Return the response
        return ResponseEntity.ok(
            FinishedCrosswordGameResponse(
                totalPoints = totalPoints,
                properFinalWord = ProperAnswer(
                    expectedAnswer = game.properAnswers.finalWord,
                    userAnswer = body.answers.finalWord,
                    score = reviewedFinalAnswer
                ),
                properQuestionsAnswers = reviewedQuestions
            )
        )
    }
}