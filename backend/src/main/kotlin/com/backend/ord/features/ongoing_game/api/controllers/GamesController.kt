package com.backend.ord.features.ongoing_game.api.controllers

import com.backend.ord.core.auth.jwt.JwtService
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_requests.CancelGameRequest
import com.backend.ord.services.GameService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/games")
class GamesController(
    private val jwtService: JwtService,
    private val gameService: GameService,
) {
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

    @DeleteMapping("/cancel/{gameId}")
    fun cancelGame(
        request: HttpServletRequest,
        @PathVariable gameId: UUID,
        @Valid @RequestBody body: CancelGameRequest
    ): ResponseEntity<Unit> {
        val user = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        gameService.cancelGame(
            gameId = gameId,
            userId = user.id,
            duration = body.duration
        )

        return ResponseEntity.noContent().build()
    }
}