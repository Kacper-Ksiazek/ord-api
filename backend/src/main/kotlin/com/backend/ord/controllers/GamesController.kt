package com.backend.ord.controllers

import com.backend.ord.api.requests.games.data.CrosswordToFinishRequestData
import com.backend.ord.api.responses.games.StartedCrosswordGameResponse
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.dto.game.CrosswordGameDTO
import com.backend.ord.domain.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.mappers.GameMapper
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.game.GameType
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.services.GameService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.backend.ord.utils.games.CrosswordUtils
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
    private val aIGameService: AIGameService,
    private val jwtService: JwtService,
    private val gameService: GameService,
    private val gameTokensUsageService: GameTokensUsageService,
    private val gameMapper: GameMapper
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
    // @PostMapping("/finish/immersive-story")

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
    ): ResponseEntity<StartedCrosswordGameResponse> {
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
    ): ResponseEntity<*> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val game: CrosswordGameDTO = gameMapper.toCrosswordDTO(
            entity = gameService.findByIdOrFail(id = body.gameId, userId = user.id)
        )

        println(game.instruction)

        // W postmanie wyklikac i utworzyc mocka crossword game do skonczenia.


        return ResponseEntity.ok("Game finished")
    }
}