package com.backend.ord.controllers.games

import com.backend.ord.api.requests.games.data.CrosswordToFinishRequestData
import com.backend.ord.api.requests.games.data.StartGameRequestData
import com.backend.ord.api.responses.games.IdentifiableProperAnswer
import com.backend.ord.api.responses.games.ProperAnswer
import com.backend.ord.api.responses.games.crossword.FinishedCrosswordGameResponse
import com.backend.ord.api.responses.games.crossword.StartedCrosswordGameResponse
import com.backend.ord.config.GamesConfig
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.enums.application.game.AnswerScore
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.services.GameReviewService
import com.backend.ord.services.GameService
import com.backend.ord.services.OngoingGameService
import com.backend.ord.services.ai.AIGameService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
    private val aiGameService: AIGameService,
    private val jwtService: JwtService,
    private val gameService: GameService,
    private val ongoingGameService: OngoingGameService,
    private val ongoingGameMapper: OngoingGameMapper,
    private val gameReviewService: GameReviewService

) {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    /**
     * Start a new crossword game
     */
    @PostMapping("/start")
    fun startCrosswordGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: StartGameRequestData
    ): ResponseEntity<StartedCrosswordGameResponse> {
        // 1. Assert the user is authenticated
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        // 2. Generate crossword game using AI
        val (properAnswers, aiGeneratedCrosswordBase) = aiGameService.generateCrosswordGame(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        // 3. Parse the generated crossword game and compute its instruction
        val instruction = CrosswordInstruction.construct(
            aiGeneratedQuestions = aiGeneratedCrosswordBase,
            difficulty = body.difficulty
        )

        // 4. Save the game in the database
        val savedGame: OngoingGame = ongoingGameService.save(
            OngoingGame(
                user = user,
                difficulty = body.difficulty,
                type = GameType.CROSSWORD,
                language = body.language,
                properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
            )
        )

        return ResponseEntity.ok(
            StartedCrosswordGameResponse(
                gameId = savedGame.id,
                instruction = instruction,

                // TODO: Remove this
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
        @Valid @RequestBody body: CrosswordToFinishRequestData
    ): ResponseEntity<FinishedCrosswordGameResponse> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val game: OngoingCrosswordGameDTO = ongoingGameMapper.toCrosswordDTO(
            entity = ongoingGameService.findByIdOrFail(id = body.gameId, userId = user.id)
        )

        // 3. Check all words forming a crossword
        val reviewedQuestions = gameReviewService.reviewUserAnswersAndUpdateDBPoints(
            user = user,
            language = game.language,
            difficulty = game.difficulty,
            expectedAnswers = game.properAnswers.questions,
            userAnswers = body.userAnswers.questionsAnswers,
        )

        // 4. Compute points received from words forming a crossword
        val pointsForQuestions: Int = GameReviewService.Companion.computeFinalScoreComponent(
            receivedPoints = reviewedQuestions.sumOf { it.userAnswerScore.wage },
            maxPoints = game.properAnswers.questions.size * AnswerScore.CORRECT.wage,
            moduleRatio = GamesConfig.Points.ScoreFactorsRatio.Crossword.QUESTIONS
        )

        // 5. Compute points received from the final word
        val reviewedFinalAnswer: AnswerScore = AnswerScore.Companion.reviewUserAnswer(
            userAnswer = body.userAnswers.answer,
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
            finalScore = totalPoints,
            duration = body.duration
        )

        // 8. Return the response
        return ResponseEntity.ok(
            FinishedCrosswordGameResponse(
                totalPoints = totalPoints,
                properFinalWord = ProperAnswer(
                    expectedAnswer = game.properAnswers.finalWord,
                    userAnswer = body.userAnswers.answer,
                    score = reviewedFinalAnswer
                ),
                properQuestionsAnswers = reviewedQuestions.map { (id, word, score) ->
                    val userAnswer = body.userAnswers.questionsAnswers.find { it.word == word }?.word

                    IdentifiableProperAnswer(
                        id = id,
                        expectedAnswer = word,
                        userAnswer = userAnswer,
                        score = score
                    )
                }
            )
        )
    }
}