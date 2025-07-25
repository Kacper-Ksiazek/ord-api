package com.ord.controllers.games

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.config.properties.JwtProperties
import com.ord.controllers.games.bases.GameControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.user.UserRepository
import com.ord.core.user.model.UserMapper
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.repository.GameTokensUsageRepository
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.mocks.games.SentencesWritingGameMocker
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- CrosswordGameController")
class TestSentencesWritingGameController @Autowired constructor(
    private val gameTokensUsageRepository: GameTokensUsageRepository,
    private val userActivityLogRepository: UserActivityLogRepository,
    private val userSeeder: UserSeeder,
    private val wordMockFactory: WordFactory,

    objectMapper: ObjectMapper,
    mockMvc: MockMvc,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userMapper: UserMapper,
    userRepository: UserRepository,

    wordRepository: WordRepository,
    ongoingGameMapper: OngoingGameMapper,
    ongoingGameRepository: OngoingGameRepository,
    finishedGameRepository: FinishedGameRepository
) : GameControllerTestBase(
    objectMapper = objectMapper,
    mockMvc = mockMvc,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userMapper = userMapper,
    userRepository = userRepository,

    wordRepository = wordRepository,
    ongoingGameMapper = ongoingGameMapper,
    ongoingGameRepository = ongoingGameRepository,
    finishedGameRepository = finishedGameRepository
) {
    val sentencesWritingGameMocker = SentencesWritingGameMocker(
        objectMapper = objectMapper,
        userMapper = userMapper,
        wordRepository = wordRepository,
        ongoingGameMapper = ongoingGameMapper,
        ongoingGameRepository = ongoingGameRepository,
        wordMockFactory = wordMockFactory,
        mockMvc = mockMvc,
    )
}