package com.backend.ord.controllers.games

import com.backend.ord.api.requests.games.data.CrosswordToFinishRequestData
import com.backend.ord.api.requests.games.data.StartGameRequestData
import com.backend.ord.api.responses.games.IdentifiableProperAnswer
import com.backend.ord.api.responses.games.ProperAnswer
import com.backend.ord.api.responses.games.crossword.FinishedCrosswordGameResponse
import com.backend.ord.api.responses.games.crossword.StartedCrosswordGameResponse
import com.backend.ord.config.AnswerScore
import com.backend.ord.config.ComponentsPointsRatio
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
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
import java.util.*

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
        val (properAnswers, aiGeneratedCrosswordBase, usedWordsIds, gpTokensUsageLogs) = aiGameService.generateCrosswordGame(
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
        val reviewedQuestions: List<Triple<UUID, String, AnswerScore>> =
            game.properAnswers.questions.entries.map { properAnswer ->
                val score = GameReviewingUtils.getPointsForUserAnswer(
                    difficulty = game.difficulty,
                    correctAnswer = properAnswer.value,
                    userAnswer = body.userAnswers.questionsAnswers.find {
                        it.id == properAnswer.key
                    }?.word,
                )

                return@map Triple(properAnswer.key, properAnswer.value, score)
            }

        // 4. Compute points received from words forming a crossword
        val pointsForQuestions: Int = GameReviewingUtils.computeFinalScoreComponent(
            receivedScoreForThisComponent = reviewedQuestions.sumOf { it.third.value },
            maxScoreForThisComponent = game.instruction.questions.size * AnswerScore.CORRECT.value,
            componentPointsRation = ComponentsPointsRatio.Crossword.QUESTIONS
        )

        // 5. Compute points received from the final word
        val reviewedFinalAnswer: AnswerScore = GameReviewingUtils.getPointsForUserAnswer(
            userAnswer = body.userAnswers.answer,
            correctAnswer = game.properAnswers.finalWord,
            difficulty = game.difficulty
        )

        val pointsForFinalAnswer: Int = GameReviewingUtils.computeFinalScoreComponent(
            receivedScoreForThisComponent = reviewedFinalAnswer.value,
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
            FinishedCrosswordGameResponse(
                totalPoints = totalPoints,
                properFinalWord = ProperAnswer(
                    expectedAnswer = game.properAnswers.finalWord,
                    userAnswer = body.userAnswers.answer,
                    result = reviewedFinalAnswer.resultName
                ),
                properQuestionsAnswers = reviewedQuestions.map { (id, word, score) ->
                    val userAnswer = body.userAnswers.questionsAnswers.find { it.word == word }?.word

                    IdentifiableProperAnswer(
                        id = id,
                        expectedAnswer = word,
                        userAnswer = userAnswer,
                        result = score.resultName
                    )
                }
            )
        )
    }
}