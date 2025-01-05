package com.backend.ord.controllers.games

import com.backend.ord.api.requests.games.data.CrosswordToFinishRequestData
import com.backend.ord.api.requests.games.data.StartGameRequestData
import com.backend.ord.api.responses.games.FinishedGameResponse
import com.backend.ord.api.responses.games.StartedCrosswordGameResponse
import com.backend.ord.config.AnswerScore
import com.backend.ord.config.ComponentsPointsRatio
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.services.GameService
import com.backend.ord.services.WordService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.backend.ord.utils.games.CrosswordUtils
import com.backend.ord.utils.games.GameReviewingUtils
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
    private val gameTokensUsageService: GameTokensUsageService,
    private val gameMapper: GameMapper,
    private val wordService: WordService
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
        val (aiGeneratedCrosswordBase, gpTokensUsageLogs, usedWordsIds, properAnswers) = aiGameService.generateCrosswordGame(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        // 3. Parse the generated crossword game and compute its instruction
        val (instruction, board) = CrosswordUtils.createInstruction(
            aiGeneratedQuestions = aiGeneratedCrosswordBase,
        )

        // 4. Save the game in the database
        val savedGame: Game = gameService.save(
            Game(
                user = user,
                difficulty = body.difficulty,
                type = GameType.CROSSWORD,
                language = body.language,
                instruction = jsonObjectMapper.writeValueAsString(instruction),
                properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
            )
        )

        // 5. Save all gpt tokens usage logs in the database
        gameTokensUsageService.assignGameToMultipleLogs(
            gptTokensUsageLogs = gpTokensUsageLogs,
            gameToAssign = savedGame
        )

        // 6. Save pivot entities for words used in the game
        gameService.saveAllWordsUsedInAGame(
            wordsIds = usedWordsIds,
            gameId = savedGame.id
        )

        // 7. Hide letters of proper answers based on the game difficulty
        instruction.questions.forEach { question ->
            question.word = CrosswordUtils.hideLettersInProperAnswer(
                wordToHide = question.word,
                difficulty = body.difficulty
            )
        }

        return ResponseEntity.ok(
            StartedCrosswordGameResponse(
                gameId = savedGame.id,
                board = board,
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
    ): ResponseEntity<FinishedGameResponse> {
        // 1. Get authenticated user data and retrieve the game from the database
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)
        val game: CrosswordGameDTO = gameMapper.toCrosswordDTO(
            entity = gameService.findByIdOrFail(id = body.gameId, userId = user.id)
        )

        // 2. Ensure the game is in progress
        if (game.status != GameStatus.IN_PROGRESS) {
            throw BadRequestException("The game's status is not in progress")
        }

        // 3. Check all words forming a crossword
        val reviewedQuestions: List<Pair<String, AnswerScore>> =
            game.properAnswers.questions.entries.map { properAnswer ->
                return@map GameReviewingUtils.getPointsForUserAnswer(
                    difficulty = game.difficulty,
                    correctAnswer = properAnswer.value,
                    userAnswer = body.userAnswers.questionsAnswers.find {
                        it.id == properAnswer.key
                    }?.word,
                )
            }

        // 4. Compute points received from words forming a crossword
        val pointsForQuestions: Int = GameReviewingUtils.computeFinalScoreComponent(
            receivedScoreForThisComponent = reviewedQuestions.sumOf { it.second.value },
            maxScoreForThisComponent = game.instruction.questions.size * AnswerScore.CORRECT.value,
            componentPointsRation = ComponentsPointsRatio.Crossword.QUESTIONS
        )

        // 5. Compute points received from the final word
        val pointsForFinalAnswer: Int = GameReviewingUtils.computeFinalScoreComponent(
            receivedScoreForThisComponent = GameReviewingUtils.getPointsForUserAnswer(
                userAnswer = body.userAnswers.answer,
                correctAnswer = game.properAnswers.finalWord,
                difficulty = game.difficulty
            ).second.value,
            maxScoreForThisComponent = AnswerScore.CORRECT.value,
            componentPointsRation = ComponentsPointsRatio.Crossword.FINAL_WORD
        )

        // 6. Add points together and compute the total score
        val totalPoints: Int = pointsForQuestions + pointsForFinalAnswer

        // 7. Update the game in the database
        gameService.finishGame(
            game = gameMapper.toEntity(game),
            finalScore = totalPoints,
            duration = body.duration
        )

        // 8. Update points for all involved words
        wordService.updatePointsForManyWords(
            userId = user.id,
            language = game.language,
            wordsAndPoints = reviewedQuestions
        )

        // 9. Return the response
        return ResponseEntity.ok(
            FinishedGameResponse(
                totalPoints = totalPoints,
                properAnswers = reviewedQuestions.map { (word, score) ->
                    val userAnswer = body.userAnswers.questionsAnswers.find { it.word == word }?.word

                    FinishedGameResponse.Companion.ProperAnswers(
                        expectedAnswer = word,
                        userAnswer = userAnswer,
                        result = score.resultName
                    )
                }
            )
        )
    }
}