package com.ord.features.game.api.controllers

import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.variants.crossword.dto.api_requests.FinishWordsTypingGameRequest
import com.ord.features.game.variants.shared.controllers.GameControllerBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
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
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val instruction = aiGameService.generateSentencesWritingGame(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        val savedGame: OngoingGameEntity = ongoingGameService.save(
            OngoingGameEntity(
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
        @Valid @RequestBody body: FinishWordsTypingGameRequest
    ): ResponseEntity<FinishedWordsTypingGameResponse> {
        TODO()
    }
}