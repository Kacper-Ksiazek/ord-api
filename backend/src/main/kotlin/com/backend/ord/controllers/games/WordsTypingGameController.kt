package com.backend.ord.controllers.games

import com.backend.ord.api.requests.games.data.StartGameRequestData
import com.backend.ord.api.responses.games.bases.StartedWordsTypingGameResponse
import com.backend.ord.controllers.games.bases.GameControllerBase
import com.backend.ord.domain.application.games.words_typing.WordsTypingInstruction
import com.backend.ord.domain.application.games.words_typing.WordsTypingQuestion
import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.utils.hideLetters
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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
    ): ResponseEntity<StartedWordsTypingGameResponse> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val (aiResponse, properAnswers) = aiGameService.generateWordsTypingGame(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        val instruction: WordsTypingInstruction = aiResponse.entries.map {
            WordsTypingQuestion(
                word = it.key.hideLetters(),
                clue = it.value
            )
        }

        val savedGame: OngoingGame = ongoingGameService.save(
            OngoingGame(
                user = user,
                difficulty = body.difficulty,
                type = GameType.WORDS_TYPING,
                language = body.language,
                properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
            )
        )

        return ResponseEntity.ok(
            StartedWordsTypingGameResponse(
                gameId = savedGame.id,
                instruction = instruction,
                properAnswers = properAnswers
            )
        )
    }


}
