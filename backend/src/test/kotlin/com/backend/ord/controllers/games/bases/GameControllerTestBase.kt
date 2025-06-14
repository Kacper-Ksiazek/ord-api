package com.backend.ord.controllers.games.bases

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.bases.ControllerTestBase
import com.backend.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.backend.ord.core.user.UserRepository
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.core.word.repository.WordRepository
import com.backend.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.backend.ord.features.game.repositories.FinishedGameRepository
import com.backend.ord.features.game.repositories.OngoingGameRepository
import com.backend.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
abstract class GameControllerTestBase(
    objectMapper: ObjectMapper,
    mockMvc: MockMvc,
    userMapper: UserMapper,
    jwtProperties: JwtProperties,
    userRepository: UserRepository,
    languageProficiencyRepository: LanguageProficiencyRepository,

    val wordRepository: WordRepository,
    val ongoingGameMapper: OngoingGameMapper,
    val ongoingGameRepository: OngoingGameRepository,
    val finishedGameRepository: FinishedGameRepository
) : ControllerTestBase(
    objectMapper = objectMapper,
    mockMvc = mockMvc,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userMapper = userMapper,
    userRepository = userRepository
) {
    val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)
}