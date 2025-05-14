package com.backend.ord.shared.controllers

import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.services.GameReviewService
import com.backend.ord.services.GameService
import com.backend.ord.services.OngoingGameService
import com.backend.ord.services.ai.AIGameService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
abstract class GameControllerBase {
    @Autowired
    protected lateinit var aiGameService: AIGameService

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