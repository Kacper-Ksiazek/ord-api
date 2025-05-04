package com.backend.ord.controllers.games

import com.backend.ord.api.responses.games.bases.StartedWordsTypingGameResponse
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.games.bases.GameControllerTestBase
import com.backend.ord.domain.persistence.dto.OngoingWordsTypingGameDTO
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.repositories.*
import com.backend.ord.repositories.gpt_tokens_usage.GameTokensUsageRepository
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.testing_utils.mocks.games.GameMockerBase
import com.backend.ord.testing_utils.mocks.games.WordsTypingGameMocker
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
        fun `Words typing game can be properly stared - function encapsulates the entire process`() {
            TODO("Implement")
        }


        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            val authenticatedUser = mockAuthenticatedUser()

            lateinit var gameSavedInDb: OngoingWordsTypingGameDTO
            lateinit var gameSentToUser: StartedWordsTypingGameResponse

            @BeforeAll
            fun beforeAll() {
                with(wordsTypingGameMocker.mockThroughApiFlow(authenticatedUser)) {
                    gameSavedInDb = first
                    gameSentToUser = second
                }
            }

            @Test
            fun `All words should be unique`() {
                with(gameSavedInDb.properAnswers.values) {
                    this.distinct().size shouldBe this.size
                }
            }

            @Test
            fun `Game returned in response is the game saved in the DB`() {
                gameSavedInDb.id shouldBe gameSentToUser.gameId
            }

            @Test
            fun `Game is properly saved in the DB`() {
                with(gameSavedInDb) {
                    type shouldBe GameType.WORDS_TYPING
                    language shouldBe GameMockerBase.Companion.DefaultParams.language
                    difficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty

                    user.id shouldBe authenticatedUser.userInfo.id
                }
            }

            @Test
            fun `Proper answers are correctly saved in the DB`() {
                gameSavedInDb.properAnswers.size shouldBe gameSentToUser.instruction.size

                gameSavedInDb.properAnswers.entries.forEach { (questionId, answer) ->
                    with(gameSentToUser.instruction.find { it.id == questionId }) {
                        this shouldNotBe null
                        this!!.word.length shouldBe answer.length
                    }
                }
            }

            @Test
            fun `GPT use logs are properly saved in the DB`() {
                val logs = gameTokensUsageRepository.findAllForUser(userId = authenticatedUser.userInfo.id)

                logs shouldHaveSize 1

                with(logs.first()) {
                    gameType shouldBe GameType.WORDS_TYPING
                    language shouldBe GameMockerBase.Companion.DefaultParams.language
                    gameDifficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty
                    consumptionType shouldBe GamesGPTTokensConsumptionType.GENERATE
                }
            }
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