package com.backend.ord.controllers.games

import com.backend.ord.config.GamesConfig
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.games.bases.GameControllerTestBase
import com.backend.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.backend.ord.core.user.UserRepository
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.core.word.repository.WordRepository
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.backend.ord.features.game.model.ongoing_game.OngoingWordsTypingGameDTO
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.enums.GameGrade
import com.backend.ord.features.game.model.ongoing_game.enums.GameType
import com.backend.ord.features.game.repositories.FinishedGameRepository
import com.backend.ord.features.game.repositories.OngoingGameRepository
import com.backend.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.backend.ord.features.game.variants.shared.enums.AnswerScore
import com.backend.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.backend.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import com.backend.ord.features.user_activity_log.model.enums.UserActivityType
import com.backend.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.backend.ord.repositories.gpt_tokens_usage.GameTokensUsageRepository
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.backend.ord.testing_utils.dto.AlteredWordProperAnswer
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.backend.ord.testing_utils.dto.toRequestBody
import com.backend.ord.testing_utils.extensions.*
import com.backend.ord.testing_utils.mocks.games.GameMockerBase
import com.backend.ord.testing_utils.mocks.games.WordsTypingGameMocker
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import java.util.*

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
            val authenticatedUser = mockAuthenticatedUser()

            lateinit var gameSavedInDb: OngoingWordsTypingGameDTO
            lateinit var gameSentToUser: StartedWordsTypingGameResponse

            with(wordsTypingGameMocker.mockThroughApiFlow(authenticatedUser)) {
                gameSavedInDb = first
                gameSentToUser = second
            }

            with(gameSavedInDb.properAnswers.values) {
                this.distinct().size shouldBe this.size
            }

            gameSavedInDb.id shouldBe gameSentToUser.gameId

            with(gameSavedInDb) {
                type shouldBe GameType.WORDS_TYPING
                language shouldBe GameMockerBase.Companion.DefaultParams.language
                difficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty

                user.id shouldBe authenticatedUser.userInfo.id
            }

            gameSavedInDb.properAnswers.size shouldBe gameSentToUser.instruction.size

            gameSavedInDb.properAnswers.entries.forEach { (questionId, answer) ->
                with(gameSentToUser.instruction.find { it.id == questionId }) {
                    this shouldNotBe null
                    this!!.word.length shouldBe answer.length
                }
            }

            val logs = gameTokensUsageRepository.findAllForUser(userId = authenticatedUser.userInfo.id)

            logs shouldNotHaveSize 0

            logs.forEach {
                it.gameType shouldBe GameType.WORDS_TYPING
                it.consumptionType shouldBe GamesGPTTokensConsumptionType.GENERATE
                it.language shouldBe GameMockerBase.Companion.DefaultParams.language
                it.gameDifficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty
            }
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

                logs shouldNotHaveSize 0

                logs.forEach {
                    it.gameType shouldBe GameType.WORDS_TYPING
                    it.consumptionType shouldBe GamesGPTTokensConsumptionType.GENERATE
                    it.language shouldBe GameMockerBase.Companion.DefaultParams.language
                    it.gameDifficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty
                }
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
                    gameType = GameType.WORDS_TYPING,
                    language = language,
                    difficulty = difficulty,
                    authenticatedUser = authenticatedUser
                )
            }

            @Test
            fun `403 - Anonymous user cannot start a words typing game`() {
                val request = gameRequestFactory.startGameRequest(
                    authenticatedUser = null,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                    it.response
                }
            }

            @Test
            fun `400 - User with no words assigned cannot start a crossword game`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val request = gameRequestFactory.startGameRequest(
                    authenticatedUser = authenticatedUser,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                    it.response
                }
            }

            @Test
            fun `400 - User with insufficient number of words assigned cannot start a crossword game`() {
                val requiredNumberOfWords = 12

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository,
                    numberOfWordsToLoad = requiredNumberOfWords - 1
                )

                val request = gameRequestFactory.startGameRequest(
                    authenticatedUser = authenticatedUser,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                    it.response
                }
            }

            @Test
            fun `400 - User with no proficiency in the language cannot start a crossword game`() {
                val unknownForUserLanguage = LanguageName.ITALIAN

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.A1
                    )
                )

                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository
                )

                val request = gameRequestFactory.startGameRequest(
                    language = unknownForUserLanguage,
                    authenticatedUser = authenticatedUser,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                    it.response
                }
            }

            @Test
            fun `400 - Cannot start a game without providing difficulty`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.C1
                    )
                )

                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository
                )

                val request = gameRequestFactory.startGameRequest(
                    difficulty = null,
                    authenticatedUser = authenticatedUser,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                    it.response
                }
            }

            @Test
            fun `400 - Cannot start a game without providing language`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.C1
                    )
                )

                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository
                )

                val request = gameRequestFactory.startGameRequest(
                    language = null,
                    authenticatedUser = authenticatedUser,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                    it.response
                }
            }
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
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var gameSavedInDb: OngoingWordsTypingGameDTO

            @BeforeEach
            fun beforeAll() {
                authenticatedUser = mockAuthenticatedUser()
                gameSavedInDb = wordsTypingGameMocker.mockFromJsonSource(authenticatedUser.userInfo).first
            }

            @AfterEach
            fun afterEach() {
                userRepository.deleteById(authenticatedUser.userInfo.id)
            }

            private fun getPerfectAnswersForQuestions(
                numberOfProperAnswers: Int? = null
            ): Set<WordUserAnswer> {
                return gameSavedInDb.properAnswers.getPerfectAnswersForQuestions(
                    numberOfProperAnswers
                )
            }

            private fun finishWordsTypingGame(
                numberOfProperAnswers: Int? = null,
                answers: Set<WordUserAnswer> = getPerfectAnswersForQuestions(
                    numberOfProperAnswers
                )
            ): FinishedWordsTypingGameResponse {
                val request = gameRequestFactory.finishGameRequest(
                    gameType = GameType.WORDS_TYPING,
                    authenticatedUser = authenticatedUser,
                    gameId = gameSavedInDb.id,
                    answers = answers
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                response.status shouldBe HttpStatus.OK.value()

                return getResponseBody<FinishedWordsTypingGameResponse>(response)
            }

            private fun assertUserActivityLogForCompletingCrossword(expectedType: UserActivityType) {
                userActivityLogRepository.assertUserActivityLogForCompletingGame(
                    expectedType = expectedType,

                    userId = authenticatedUser.userInfo.id,
                    language = gameSavedInDb.language,
                    difficulty = gameSavedInDb.difficulty
                )
            }

            private fun assertPointsForMistakesWereAssignedProperly(
                response: FinishedWordsTypingGameResponse,
                alteredAnswers: Set<AlteredWordProperAnswer>
            ) {
                response.properAnswers.assertPointsForMistakesWereAssignedProperly(alteredAnswers)
            }

            private fun assertDBPointsWereUpdatedProperly(
                response: FinishedWordsTypingGameResponse,
                alteredAnswers: Set<AlteredWordProperAnswer> = emptySet()
            ) {
                wordRepository.assertDBPointsWereUpdatedProperly(
                    words = gameSavedInDb.properAnswers.values.toSet(),
                    language = gameSavedInDb.language,
                    userId = authenticatedUser.userInfo.id,
                    properAnswers = response.properAnswers,
                    alteredAnswers = alteredAnswers
                )
            }

            @Test
            fun `200 - Ongoing game should be removed and finished game should be created instead`() {
                finishedGameRepository.findAllForUser(authenticatedUser.userInfo.id).shouldHaveSize(0)
                ongoingGameRepository.findByIdOrNull(gameSavedInDb.id) shouldNotBe null

                finishWordsTypingGame()

                ongoingGameRepository.findByIdOrNull(gameSavedInDb.id) shouldBe null
                finishedGameRepository.findAllForUser(authenticatedUser.userInfo.id).shouldHaveSize(1)
            }

            @Test
            fun `200 - Proper user activity log should be assigned after completing the game flawlessly`() {
                finishWordsTypingGame()

                assertUserActivityLogForCompletingCrossword(UserActivityType.WORDS_TYPING_GAME_COMPLETED_FLAWLESSLY)
            }

            @Test
            fun `200 - Proper user activity log should be assigned after completing the game with mistakes`() {
                finishWordsTypingGame(
                    numberOfProperAnswers = 9
                )

                assertUserActivityLogForCompletingCrossword(UserActivityType.WORDS_TYPING_GAME_COMPLETED_WITH_MISTAKES)
            }

            @Test
            fun `200 - Grade can be achieved - S`() {
                val response = finishWordsTypingGame()

                response.finalScore shouldBe 100.0
                response.grade shouldBe GameGrade.S
            }

            @Test
            fun `200 - Grade can be achieved - A`() {
                val response = finishWordsTypingGame(
                    numberOfProperAnswers = 15
                )

                response.grade shouldBe GameGrade.A
            }

            @Test
            fun `200 - Grade can be achieved - B`() {
                val response = finishWordsTypingGame(
                    numberOfProperAnswers = 12
                )

                response.grade shouldBe GameGrade.B
            }

            @Test
            fun `200 - Grade can be achieved - C`() {
                val response = finishWordsTypingGame(
                    numberOfProperAnswers = 10
                )

                response.grade shouldBe GameGrade.C
            }

            @Test
            fun `200 - Grade can be achieved - D`() {
                val response = finishWordsTypingGame(
                    numberOfProperAnswers = 0
                )

                response.grade shouldBe GameGrade.D
            }

            @Test
            fun `200 - Mistakes in user's answer should be corrected properly`() {
                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        AnswerScore.INCORRECT to 3,
                    )
                )

                val response = finishWordsTypingGame(
                    answers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertPointsForMistakesWereAssignedProperly(response, alteredAnswers)
            }

            @Test
            fun `200 - Points should be properly assigned - CORRECT`() {
                val response = finishWordsTypingGame()

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Points should be properly assigned - HALF_CORRECT`() {
                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        AnswerScore.HALF_CORRECT to 3,
                    )
                )

                val response = finishWordsTypingGame(
                    answers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Points should be properly assigned - INCORRECT`() {
                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        AnswerScore.INCORRECT to 3,
                    )
                )

                val response = finishWordsTypingGame(
                    answers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Points should be properly assigned - both HALF_CORRECT and INCORRECT`() {
                gameSavedInDb = wordsTypingGameMocker.mockFromJsonSource(
                    userDTO = authenticatedUser.userInfo,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        AnswerScore.HALF_CORRECT to 3,
                        AnswerScore.INCORRECT to 3,
                    )
                )

                val response = finishWordsTypingGame(
                    answers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Words can be marked as completed`() {
                gameSavedInDb = wordsTypingGameMocker.mockFromJsonSource(
                    userDTO = authenticatedUser.userInfo,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                wordRepository.saveAll(
                    wordRepository.findAllForUser(authenticatedUser.userInfo.id).map {
                        it.copy(
                            points = GamesConfig.Points.COMPLETE_WORD_THRESHOLD - 1
                        )
                    }
                )

                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        AnswerScore.HALF_CORRECT to 3,
                    )
                )

                finishWordsTypingGame(
                    answers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                val wordsUsedInGame = perfectAnswers.map { it.answer }

                wordRepository
                    .findAllForUser(authenticatedUser.userInfo.id)
                    .filter { it.origin in wordsUsedInGame }
                    .forEach {
                        it.points shouldBeGreaterThanOrEqual GamesConfig.Points.COMPLETE_WORD_THRESHOLD
                        it.isCompleted shouldBe true
                    }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            internal fun GameRequestFactory.finishGameRequest(
                gameId: UUID,
                authenticatedUser: MockedAuthenticatedUser?,
                answers: Set<WordUserAnswer> = emptySet()
            ): MockHttpServletRequestBuilder {
                return finishGameRequest(
                    gameType = GameType.WORDS_TYPING,
                    authenticatedUser = authenticatedUser,
                    gameId = gameId,
                    answers = answers
                )
            }

            @Test
            fun `403 - Anonymous user cannot finish a words typing game`() {
                val request = gameRequestFactory.finishGameRequest(
                    authenticatedUser = null,
                    gameId = UUID.randomUUID(),
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                    it.response
                }
            }

            @Test
            fun `404 - User cannot finish a game that does not belong to them`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val gameSavedInDb = wordsTypingGameMocker.mockFromJsonSource(
                    userDTO = userMapper.toDTO(userSeeder.seedOneEntity()),
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val request = gameRequestFactory.finishGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = gameSavedInDb.id,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                    it.response
                }
            }

            @Test
            fun `400 - Words typing game cannot be finished with user answer to single word over 255 characters long`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val gameSavedInDb = wordsTypingGameMocker.mockFromJsonSource(
                    userDTO = userMapper.toDTO(userSeeder.seedOneEntity()),
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val request = gameRequestFactory.finishGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = gameSavedInDb.id,
                    answers = setOf(
                        WordUserAnswer(
                            id = UUID.randomUUID(),
                            answer = "x".repeat(256)
                        )
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                    it.response
                }
            }
        }
    }
}