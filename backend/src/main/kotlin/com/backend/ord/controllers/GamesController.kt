package com.backend.ord.controllers

import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.utils.games.CrosswordUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// An irrelevant extension function to print the board
private fun MutableList<MutableList<String?>>.print() {
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
    // TODO: Prepare game repository and service and store the game in the database
) {
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
    ): ResponseEntity<*> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val aiGeneratedCrosswordBase: AIGeneratedCrossword = aIGameService.generateCrosswordGame(
            user = user,
            language = LanguageName.ENGLISH,
            difficulty = GameDifficulty.HARD
        )

        val board: MutableList<MutableList<String?>> = CrosswordUtils.createBoard(
            aiGeneratedQuestions = aiGeneratedCrosswordBase,
        )

        board.print()

        // TODO: Prepare a proper ( yet undefined ) response body and hash the answers
        return ResponseEntity.ok(
            board
        )
    }
}