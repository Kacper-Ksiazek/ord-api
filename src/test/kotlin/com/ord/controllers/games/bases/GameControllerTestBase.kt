package com.ord.controllers.games.bases

/*

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.user.model.UserMapper
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.testing_utils.api_requests_factories.GameRequestFactory
import tools.jackson.databind.json.JsonMapper
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
abstract class GameControllerTestBase(
    objectMapper: JsonMapper,
    mockMvc: MockMvc,
    userMapper: UserMapper,
    sessionProperties: SessionProperties,
    userRepository: UserRepository,
    languageProficiencyRepository: LanguageProficiencyRepository,

    val wordRepository: WordRepository,
    val ongoingGameMapper: OngoingGameMapper,
    val ongoingGameRepository: OngoingGameRepository,
    val finishedGameRepository: FinishedGameRepository,
) : ControllerTestBase(
    objectMapper = objectMapper,
    mockMvc = mockMvc,
    sessionProperties = sessionProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userMapper = userMapper,
    userRepository = userRepository
) {
    val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)
}

*/