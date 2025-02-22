package com.backend.ord.controllers.games

import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.services.GameService
import com.backend.ord.services.WordService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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

    // Endpoint to return all available games with their difficulties for the user in a given language. It should retrieve the number of words they have in the DB and then
    // return the information which games can be played with the number of words they have.

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
}