package com.backend.ord.controllers.games

import com.backend.ord.api.requests.games.StartGameRequest
import com.backend.ord.api.requests.games.WordsTypingToFinishRequest
import com.backend.ord.api.responses.games.FinishedWordsTypingGameResponse
import com.backend.ord.api.responses.games.bases.StartedWordsTypingGameResponse
import com.backend.ord.controllers.games.bases.GameControllerBase
import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameType
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games/sentences-writting")
class SentencesWritingGameController : GameControllerBase() {

    @PostMapping("/start")
    fun startWordsTypingGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: StartGameRequest
    ): ResponseEntity<StartedWordsTypingGameResponse> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val instruction = aiGameService.generateSentencesWritingGame(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        val savedGame: OngoingGame = ongoingGameService.save(
            OngoingGame(
                user = user,
                type = GameType.WORDS_TYPING,

                language = body.language,
                difficulty = body.difficulty,
                properAnswers = jsonObjectMapper.writeValueAsString(null)
            )
        )

        return ResponseEntity.ok(
            TODO()
        )
    }

    @PostMapping("/finish")
    fun finishWordsTypingGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: WordsTypingToFinishRequest
    ): ResponseEntity<FinishedWordsTypingGameResponse> {
        TODO()
    }
}
