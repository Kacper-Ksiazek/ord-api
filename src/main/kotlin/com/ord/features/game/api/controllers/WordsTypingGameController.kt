package com.ord.features.game.api.controllers

import com.ord.core.auth.security.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.features.game.variants.crossword.dto.api_requests.FinishWordsTypingGameRequest
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.words_typing.api.WordsTypingGameFacade
import com.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games/words-typing")
class WordsTypingGameController(
    private val wordsTypingGameFacade: WordsTypingGameFacade
) {
    @PostMapping("/start")
    fun startWordsTypingGame(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: StartGameRequest
    ): ResponseEntity<StartedWordsTypingGameResponse> = wordsTypingGameFacade.startGame(user = user, body = body)

    @PostMapping("/finish")
    fun finishWordsTypingGame(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: FinishWordsTypingGameRequest
    ): ResponseEntity<FinishedWordsTypingGameResponse> = wordsTypingGameFacade.finishGame(user, body)
}