package com.backend.ord.controllers

import com.backend.ord.api.requests.games.data.CrosswordToFinishRequestData
import com.backend.ord.api.responses.games.StartedCrosswordGameResponse
import com.backend.ord.config.AnswerScore
import com.backend.ord.config.ComponentsPointsRatio
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.dto.game.CrosswordGameDTO
import com.backend.ord.domain.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.mappers.GameMapper
import com.backend.ord.enums.persistance.game.GameDifficulty
import com.backend.ord.enums.persistance.game.GameStatus
import com.backend.ord.enums.persistance.game.GameType
import com.backend.ord.enums.persistance.language.LanguageName
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

// An irrelevant extension function to print the board
private fun List<List<String?>>.print() {
    print('+')
    repeat(this[0].size) { print("-") }
    println('+')
    this.forEach { row ->
        print("|")
        row.forEach { cell ->
            print("${cell ?: ' '} ")
        }
        print("|")
        println()
    }
    print('+')
    repeat(this[0].size) { print("-") }
    println('+')
}


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
        val (aiGeneratedCrosswordBase, gpTokensUsageLogs) = aiGameService.generateCrosswordGame(
            user = user,
            language = LanguageName.ENGLISH,
            difficulty = GameDifficulty.HARD
        )

        // 3. Parse the generated crossword game and compute its instruction
        val instruction: CrosswordInstruction = CrosswordUtils.createInstruction(
            aiGeneratedQuestions = aiGeneratedCrosswordBase,
        )

        // TODO: Remove board from instruction saved in the database
//        instruction.board.print()

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
        // TODO: Create pivot entities for games used in the game


        return ResponseEntity.ok(
            StartedCrosswordGameResponse(
                gameId = savedGame.id,
                instruction = instruction
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
    ): ResponseEntity<Unit> {
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
                    userAnswer = body.userAnswers.questionsAnswers.find { it.coordinates == questionFromInstruction.coordinates }?.word,
                    correctAnswer = questionFromInstruction.word,
                    difficulty = game.difficulty
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

        // Return HTTP 204
        // TODO: Create a proper response data class
        return ResponseEntity.noContent().build()
    }

}