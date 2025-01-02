package com.backend.ord.controllers

import com.backend.ord.api.requests.games.data.CrosswordToFinishRequestData
import com.backend.ord.api.responses.games.FinishedGameResponse
import com.backend.ord.api.responses.games.StartedCrosswordGameResponse
import com.backend.ord.config.AnswerScore
import com.backend.ord.config.ComponentsPointsRatio
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.services.GameService
import com.backend.ord.services.WordService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.backend.ord.utils.games.CrosswordUtils
import com.backend.ord.utils.games.GameReviewingUtils
import com.backend.ord.utils.games.GameReviewingUtils.getPointsForUserAnswer
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
@RequestMapping("/api/v1/games")
class GamesController(
    private val aiGameService: AIGameService,
    private val jwtService: JwtService,
    private val gameService: GameService,
    private val gameTokensUsageService: GameTokensUsageService,
    private val gameMapper: GameMapper,
    private val wordService: WordService
) {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    // +-----------------------------+
    // |    GAMES ENDPOINTS PLAN:    |
    // +-----------------------------+

    // ----
    // 1. Start a game endpoints
    //
    // 1. @PostMapping("/start/words-typing")
    // 2. @PostMapping("/start/gaps-filling")
    // 3. @PostMapping("/start/sentences-writing")
    //
    // 4. @PostMapping("/start/immersive-story")

    // ----
    // 2. Review a game endpoints
    // ----
    // 5. [✅] @PostMapping("/finish/crossword")
    // 6. @PostMapping("/finish/words-typing")
    // 7. @PostMapping("/finish/gaps-filling")
    // 8. @PostMapping("/finish/sentences-writing")
    //
    // 9. @PostMapping("/finish/immersive-story")

    // ----
    // 3. General game endpoints ( ALL MVP )
    // ----
    // 10. @PostMapping("/pause/{gameId}")
    // 11. @PostMapping("/resume/{gameId}")
    // 12. [MVP] @PostMapping("/cancel/{gameId}")
    // 13. @GetMapping("/paused")
    // 14. @GetMapping("/games-history")
    // 15. @GetMapping("/statistics")

    /**
     * Start a crossword game
     */
    @PostMapping("/start/crossword")
    fun startCrosswordGame(
        request: HttpServletRequest
        // TODO: Implement game start body, including difficulty and language
    ): ResponseEntity<StartedCrosswordGameResponse> {
        // 1. Assert the user is authenticated
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        // 2. Generate crossword game using AI
        val (aiGeneratedCrosswordBase, gpTokensUsageLogs, usedWordsIds, properAnswers) = aiGameService.generateCrosswordGame(
            user = user,
            language = LanguageName.ENGLISH,
            difficulty = GameDifficulty.HARD
        )

        // 3. Parse the generated crossword game and compute its instruction
        val (instruction, board) = CrosswordUtils.createInstruction(
            aiGeneratedQuestions = aiGeneratedCrosswordBase,
        )

        // 4. Save the game in the database
        val savedGame: Game = gameService.save(
            Game(
                user = user,
                difficulty = GameDifficulty.HARD,
                type = GameType.CROSSWORD,
                language = LanguageName.ENGLISH,
                instruction = jsonObjectMapper.writeValueAsString(instruction)
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
                difficulty = GameDifficulty.HARD
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
    @PostMapping("/finish/crossword")
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
            game.instruction.questions.map { questionFromInstruction ->
                return@map getPointsForUserAnswer(
                    difficulty = game.difficulty,
                    correctAnswer = questionFromInstruction.word,
                    userAnswer = body.userAnswers.questionsAnswers.find {
                        it.coordinates.toString() == questionFromInstruction.coordinates.toString()
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
            receivedScoreForThisComponent = getPointsForUserAnswer(
                userAnswer = body.userAnswers.answer,
                correctAnswer = game.instruction.answer,
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