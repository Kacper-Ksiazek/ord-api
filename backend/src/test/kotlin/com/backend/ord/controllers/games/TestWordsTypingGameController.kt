package com.backend.ord.controllers.games

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.games.bases.GameControllerTestBase
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.repositories.*
import com.backend.ord.repositories.gpt_tokens_usage.GameTokensUsageRepository
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.testing_utils.mocks.games.WordsTypingGameMocker
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- WordsTypingGameController")
class TestWordsTypingGameController @Autowired constructor(
    private val gameTokensUsageRepository: GameTokensUsageRepository,
    private val userActivityLogRepository: UserActivityLogRepository,
    private val userSeeder: UserSeeder,
    private val wordMockFactory: WordMockFactory,

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
    val wordsTypingGameMocker = WordsTypingGameMocker(
        objectMapper = objectMapper,
        userMapper = userMapper,
        wordRepository = wordRepository,
        ongoingGameMapper = ongoingGameMapper,
        ongoingGameRepository = ongoingGameRepository,
        wordMockFactory = wordMockFactory,
        mockMvc = mockMvc,
    )

    @Nested
    @DisplayName("[POST] /api/v1/games/words-typing - start a new words typing game")
    inner class StartWordsTypingGame {

        @RepeatedTest(100)
        @Disabled("This test is disabled for automatic execution. Run manually when needed.")
        fun `Crossword can be properly stared - function encapsulates the entire process`() {
            TODO("Implement")
        }


        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            // TODO
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            // TODO
        }

    }

    @Nested
    @DisplayName("[POST] /api/v1/games/words-typing/finish - finish an ongoing words typing game")
    inner class FinishWordsTypingGame {

        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

        }
    }
}