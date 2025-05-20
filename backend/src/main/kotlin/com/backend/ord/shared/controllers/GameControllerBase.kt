package com.backend.ord.shared.controllers

import com.backend.ord.core.auth.jwt.JwtService
import com.backend.ord.features.ongoing_game.ai.generate.service.AIGenerateGameService
import com.backend.ord.features.ongoing_game.model.OngoingGameMapper
import com.backend.ord.features.ongoing_game.service.OngoingGameService
import com.backend.ord.services.GameReviewService
import com.backend.ord.services.GameService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
abstract class GameControllerBase {
    @Autowired
    protected lateinit var aiGameService: AIGenerateGameService

    @Autowired
    protected lateinit var jwtService: JwtService

    @Autowired
    protected lateinit var gameService: GameService

    @Autowired
    protected lateinit var ongoingGameService: OngoingGameService

    @Autowired
    protected lateinit var ongoingGameMapper: OngoingGameMapper

    @Autowired
    protected lateinit var gameReviewService: GameReviewService

    protected val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()
}