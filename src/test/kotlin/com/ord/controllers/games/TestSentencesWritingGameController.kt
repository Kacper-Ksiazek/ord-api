package com.ord.controllers.games

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.config.properties.JwtProperties
import com.ord.controllers.games.bases.GameControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.UserRepository
import com.ord.core.user.model.UserMapper
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.OngoingSentencesWritingGameDTO
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.repository.GameTokensUsageRepository
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.mocks.games.GameMockerBase
import com.ord.testing_utils.mocks.games.SentencesWritingGameMocker
import com.ord.utils.resource_readers.loadWordsFromResourceFile
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- SentencesWritingGameController")
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

    @Nested
    @DisplayName("[POST] /api/v1/games/sentences-writing/start - start a new sentences writing game")
    inner class StartSentencesWritingGame {
        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var gameSavedInDb: OngoingSentencesWritingGameDTO
            lateinit var gameSentToUser: StartedSentencesWritingGameResponse

            @BeforeAll
            fun beforeAll() {
                authenticatedUser = mockAuthenticatedUser()
                // Replace with actual mocker for sentences writing game
                with(sentencesWritingGameMocker.mockThroughApiFlow(authenticatedUser)) {
                    gameSavedInDb = first
                    gameSentToUser = second
                }
            }

            @Test
            fun `Game is properly saved in the DB`() {
                gameSavedInDb.id shouldBe gameSentToUser.gameId

                gameSavedInDb.type shouldBe GameType.SENTENCES_WRITING
                gameSavedInDb.language shouldBe GameMockerBase.Companion.DefaultParams.language
                gameSavedInDb.difficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty

                gameSavedInDb.user.id shouldBe authenticatedUser.userInfo.id
            }

            @Test
            fun `Proper answers (instructions) are correctly saved in the DB`() {
                gameSentToUser.instruction.size shouldBe gameSavedInDb.properAnswers.size

                gameSentToUser.instruction.forEach { instruction ->
                    gameSavedInDb.properAnswers.any { it.id == instruction.id } shouldBe true
                }
            }

            @Test
            fun `GPT use logs are properly saved in the DB`() {
                val logs = gameTokensUsageRepository.findAllForUser(userId = authenticatedUser.userInfo.id)

                logs shouldNotHaveSize 0

                logs.forEach {
                    it.gameType shouldBe GameType.SENTENCES_WRITING
                    it.consumptionType shouldBe GamesGPTTokensConsumptionType.GENERATE
                    it.language shouldBe GameMockerBase.Companion.DefaultParams.language
                    it.gameDifficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty
                }
            }

            @Test
            fun `All words should be unique`() {
                gameSavedInDb.properAnswers.map { it.word }.distinct().size shouldBe gameSavedInDb.properAnswers.size
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            internal fun GameRequestFactory.startGameRequest(
                authenticatedUser: MockedAuthenticatedUser?,
                language: LanguageName? = LanguageName.ENGLISH,
                difficulty: GameDifficulty? = GameDifficulty.HARD,
            ): MockHttpServletRequestBuilder {
                return startGameRequest(
                    gameType = GameType.SENTENCES_WRITING,
                    language = language,
                    difficulty = difficulty,
                    authenticatedUser = authenticatedUser
                )
            }

            @Test
            fun `403 - Anonymous user cannot start a sentences writing game`() {
                val request = gameRequestFactory.startGameRequest(
                    authenticatedUser = null,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe org.springframework.http.HttpStatus.FORBIDDEN.value()
                }
            }

            @Test
            fun `400 - User with no words assigned cannot start a sentences writing game`() {
                val authenticatedUser = mockAuthenticatedUser()
                val request = gameRequestFactory.startGameRequest(
                    authenticatedUser = authenticatedUser,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe org.springframework.http.HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - User with insufficient number of words assigned cannot start a sentences writing game`() {
                val requiredNumberOfWords = 6
                val authenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository,
                    numberOfWordsToLoad = requiredNumberOfWords - 1
                )

                val request = gameRequestFactory.startGameRequest(
                    authenticatedUser = authenticatedUser,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe org.springframework.http.HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - User with no proficiency in the language cannot start a sentences writing game`() {
                val unknownForUserLanguage = com.ord.core.langugae_proficiency.model.enums.LanguageName.ITALIAN
                val authenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository
                )

                val request = gameRequestFactory.startGameRequest(
                    language = unknownForUserLanguage,
                    authenticatedUser = authenticatedUser,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe org.springframework.http.HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Cannot start a game without providing difficulty`() {
                val authenticatedUser = mockAuthenticatedUser()
                com.ord.utils.resource_readers.loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository
                )
                val request = gameRequestFactory.startGameRequest(
                    difficulty = null,
                    authenticatedUser = authenticatedUser,
                )
                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe org.springframework.http.HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Cannot start a game without providing language`() {
                val authenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository
                )

                val request = gameRequestFactory.startGameRequest(
                    language = null,
                    authenticatedUser = authenticatedUser,
                )
                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe org.springframework.http.HttpStatus.BAD_REQUEST.value()
                }
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/games/sentences-writing/finish - finish an ongoing sentences writing game")
    inner class FinishSentencesWritingGame {
        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            // Placeholders for positive test methods
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            // Placeholders for negative test methods
        }
    }
}