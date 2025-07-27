package com.ord.features.game.api.controllers

import com.ord.core.auth.security.AuthenticatedUser
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.variants.sentences_writing.api.SentencesWritingGameFacade
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games/sentences-writing")
class SentencesWritingGameController(
    private val sentencesWritingGameFacade: SentencesWritingGameFacade
) {

    @PostMapping("/start")
    fun startWordsTypingGame(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: StartGameRequest
    ): ResponseEntity<StartedSentencesWritingGameResponse> = sentencesWritingGameFacade.startGame(user, body)

    @PostMapping("/finish")
    fun finishWordsTypingGame(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: FinishSentencesWritingGameRequest
    ): ResponseEntity<FinishedSentencesWritingGameResponse> {
        body.answers.forEach {
            if (it.value.length > 1024) {
                throw BadRequestException("Each sentence answer must up to 1024 characters, but got ${it.value.length} characters.")
            }
        }

        return sentencesWritingGameFacade.finishGame(user, body)
    }
}