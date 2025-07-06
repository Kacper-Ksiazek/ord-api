package com.ord.features.game.variants.shared.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.auth.jwt.JwtService
import com.ord.features.game.ai.generate.service.AIGenerateGameService
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.services.OngoingGameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Deprecated("Use GameFacadeBase instead")
abstract class GameControllerBase {
    @Autowired
    protected lateinit var aiGameService: AIGenerateGameService

    @Autowired
    protected lateinit var jwtService: JwtService

    @Autowired
    protected lateinit var ongoingGameService: OngoingGameService

    @Autowired
    protected lateinit var ongoingGameMapper: OngoingGameMapper

    @Autowired
    protected lateinit var gameReviewService: GameReviewService

    protected val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()
}