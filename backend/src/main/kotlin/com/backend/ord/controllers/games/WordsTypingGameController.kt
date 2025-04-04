package com.backend.ord.controllers.games

import com.backend.ord.api.requests.games.data.StartGameRequestData
import com.backend.ord.controllers.games.bases.GameControllerBase
import com.backend.ord.domain.persistence.entities.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games/words-typing")
class WordsTypingGameController : GameControllerBase() {

    @PostMapping("/start")
    fun startCrosswordGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: StartGameRequestData
//    ): ResponseEntity<StartedCrosswordGameResponse> {
    ) {
        // 1. Assert the user is authenticated
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        // 2. Generate crossword game using AI
        val (aiResponse, properAnswers) = aiGameService.generateCrosswordGame(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )
    }
}
