package com.ord.features.game.variants.shared.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.services.OngoingGameService
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.*

abstract class GameFacadeBase<TCreatedGameResponse, TFinishGameRequest, TFinishedGameResponse> {
    /**
     * Starts a new game.
     *
     * @param user The authenticated user who is starting the game.
     * @param body The request body containing game start parameters.
     * @return A response entity containing the created game response.
     */
    abstract fun startGame(
        userId: UUID,
        body: StartGameRequest
    ): Mono<ResponseEntity<TCreatedGameResponse>>

    /**
     * Finishes an ongoing game.
     *
     * @param user The authenticated user who is finishing the game.
     * @param body The request body containing game finish parameters.
     * @return A response entity containing the finished game response.
     */
    abstract fun finishGame(
        userId: UUID,
        body: TFinishGameRequest
    ): Mono<ResponseEntity<TFinishedGameResponse>>

    @Autowired
    protected lateinit var ongoingGameService: OngoingGameService

    @Autowired
    protected lateinit var ongoingGameMapper: OngoingGameMapper

    @Autowired
    protected lateinit var gameReviewService: GameReviewService

    protected val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()
}