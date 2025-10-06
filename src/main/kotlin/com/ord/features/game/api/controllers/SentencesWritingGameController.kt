package com.ord.features.game.api.controllers

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
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
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/games/sentences-writing")
class SentencesWritingGameController(
    private val sentencesWritingGameFacade: SentencesWritingGameFacade
) {

    @PostMapping("/start")
    fun startWordsTypingGame(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: StartGameRequest
    ): Mono<ResponseEntity<StartedSentencesWritingGameResponse>> = sentencesWritingGameFacade.startGame(user.id, body)

    @PostMapping("/finish")
    fun finishWordsTypingGame(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: FinishSentencesWritingGameRequest
    ): Mono<ResponseEntity<FinishedSentencesWritingGameResponse>> {
        body.answers.forEach {
            if (it.value.length > 1024) {
                throw BadRequestException("Each sentence answer must up to 1024 characters, but got ${it.value.length} characters.")
            }
        }

        return sentencesWritingGameFacade.finishGame(user.id, body)
    }
}