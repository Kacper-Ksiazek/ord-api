package com.backend.ord.controllers

import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.GameType
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.services.GameService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.backend.ord.utils.games.CrosswordUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
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
    private val aIGameService: AIGameService,
    private val jwtService: JwtService,
    private val gameService: GameService,
    private val gameTokensUsageService: GameTokensUsageService

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
    // 4. @PostMapping("/start/immersive-story") // WARNING: IN FUTURE SPRINTS

    // ----
    // 2. Review a game endpoints
    // ----
    // 5. @PostMapping("/review/crossword") // WARNING: MVP
    // 6. @PostMapping("/review/words-typing")
    // 7. @PostMapping("/review/gaps-filling")
    // 8. @PostMapping("/review/sentences-writing")
    //
    // @PostMapping("/review/immersive-story") // WARNING: IN FUTURE SPRINTS

    // ----
    // 3. General game endpoints ( ALL MVP )
    // ----
    // 10. @PostMapping("/pause/{gameId}") // WARNING: MVP
    // 11. @PostMapping("/resume/{gameId}") // WARNING: MVP
    // 12. @PostMapping("/cancel/{gameId}") // WARNING: MVP
    // 13. @GetMapping("/paused") // WARNING: MVP
    // 14. @GetMapping("/games-history") // WARNING: MVP
    // 15. @GetMapping("/statistics") // WARNING: MVP

    /**
     * Start a crossword game
     */
    @PostMapping("/start/crossword")
    fun startGame(
        request: HttpServletRequest
    ): ResponseEntity<CrosswordInstruction> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val (aiGeneratedCrosswordBase, gpTokensUsageLogs) = aIGameService.generateCrosswordGame(
            user = user,
            language = LanguageName.ENGLISH,
            difficulty = GameDifficulty.HARD
        )

        val instruction: CrosswordInstruction = CrosswordUtils.createInstruction(
            aiGeneratedQuestions = aiGeneratedCrosswordBase,
        )

        instruction.board.print()

        val savedGame: Game = gameService.save(
            Game(
                user = user,
                difficulty = GameDifficulty.HARD,
                type = GameType.CROSSWORD,
                instruction = jsonObjectMapper.writeValueAsString(instruction)
            )
        )

        gameTokensUsageService.assignGameToMultiple(
            gptTokensUsageLogs = gpTokensUsageLogs,
            gameToAssign = savedGame
        )

        return ResponseEntity.ok(instruction)
    }
}