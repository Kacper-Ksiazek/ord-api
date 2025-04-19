package com.backend.ord.controllers.helpers.utils_for_testing.bases

import com.backend.ord.controllers.helpers.request_factories.GameRequestFactory
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.repositories.FinishedGameRepository
import com.backend.ord.repositories.OngoingGameRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.mocks.games.MockCrosswordGamesFromJSON
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

@AutoConfigureMockMvc
abstract class GameControllerTestBase(
    objectMapper: ObjectMapper
) : ControllerTestBase(objectMapper = objectMapper) {
    @Autowired
    lateinit var ongoingGameMapper: OngoingGameMapper

    @Autowired
    lateinit var wordRepository: WordRepository

    @Autowired
    lateinit var ongoingGameRepository: OngoingGameRepository

    @Autowired
    lateinit var finishedGameRepository: FinishedGameRepository

    @Autowired
    lateinit var mockCrosswordGames: MockCrosswordGamesFromJSON

    val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)
}