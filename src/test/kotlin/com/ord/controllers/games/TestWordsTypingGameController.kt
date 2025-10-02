package com.ord.controllers.games

import com.ord.config.GamesConfig
import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.core.user.model.UserMapper
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.OngoingWordsTypingGameDTO
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameGrade
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_requests.UnsafeStartGameRequestData
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import com.ord.features.game.variants.words_typing.dto.api_requests.FinishWordsTypingGameRequest
import com.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api.clients.games.WordsTypingGameAPIClient
import com.ord.testing_utils.dto.AlteredWordProperAnswer
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.dto.toRequestBody
import com.ord.testing_utils.extensions.*
import com.ord.testing_utils.mocks.games.WordsTypingGameMocker
import com.ord.utils.resource_readers.loadWordsFromResourceFile
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@DisplayName("- WordsTypingGameController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000") // 3 minutes
class TestWordsTypingGameController @Autowired constructor(
    private val userActivityLogRepository: UserActivityLogRepository,
    private val userSeeder: UserSeeder,
    private val wordMockFactory: WordFactory,
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val wordRepository: WordRepository,
    private val ongoingGameMapper: OngoingGameMapper,
    private val ongoingGameRepository: OngoingGameRepository,
    private val finishedGameRepository: FinishedGameRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository
) {
    private val wordsTypingGameAPIClient = WordsTypingGameAPIClient(webClient)

    private val wordsTypingGameMocker = WordsTypingGameMocker(
        apiClient = wordsTypingGameAPIClient,
        ongoingGameMapper = ongoingGameMapper,
        ongoingGameRepository = ongoingGameRepository,
        wordMockFactory = wordMockFactory,
        wordRepository = wordRepository
    )

    @Nested
    @DisplayName("[POST] /api/v1/games/words-typing - start a new words typing game")
    inner class StartWordsTypingGame {

        @RepeatedTest(100)
        @Disabled("This test is disabled for automatic execution. Run manually when needed.")
        fun `Words typing game can be properly stared - function encapsulates the entire process`() {
            val authenticatedUser = mockAuthenticatedUser()

            loadWordsFromResourceFile(
                userId = authenticatedUser.userInfo.id,
                wordsRepository = wordRepository
            )

            val gameResponse = wordsTypingGameAPIClient.startGame(
                body = StartGameRequest(
                    language = LanguageName.ENGLISH,
                    difficulty = GameDifficulty.HARD
                ),
                user = authenticatedUser
            )

            gameResponse.status shouldBe HttpStatus.OK
            gameResponse.body shouldNotBe null

            val gameSentToUser = gameResponse.body!!

            val gameSavedInDb: OngoingWordsTypingGameDTO = ongoingGameMapper.toWordsTypingDTO(
                entity = ongoingGameRepository.findById(gameSentToUser.gameId).block()!!
            )

            with(gameSavedInDb.properAnswers.values) {
                this.distinct().size shouldBe this.size
            }

            gameSavedInDb.id shouldBe gameSentToUser.gameId

            with(gameSavedInDb) {
                type shouldBe GameType.WORDS_TYPING
                language shouldBe LanguageName.ENGLISH
                difficulty shouldBe GameDifficulty.HARD

                userId shouldBe authenticatedUser.userInfo.id
            }

            gameSavedInDb.properAnswers.size shouldBe gameSentToUser.instruction.size

            gameSavedInDb.properAnswers.entries.forEach { (questionId, answer) ->
                with(gameSentToUser.instruction.find { it.id == questionId }) {
                    this shouldNotBe null
                    this!!.word.length shouldBe answer.length
                }
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
                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val gameResponse = wordsTypingGameAPIClient.startGame(
                    body = StartGameRequest(
                        language = LanguageName.ENGLISH,
                        difficulty = GameDifficulty.HARD
                    ),
                    user = authenticatedUser
                )

                gameResponse.status shouldBe HttpStatus.OK
                gameResponse.body shouldNotBe null

                gameSentToUser = gameResponse.body!!
                gameSavedInDb = ongoingGameMapper.toWordsTypingDTO(
                    entity = ongoingGameRepository.findById(gameSentToUser.gameId).block()!!
                )
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
                    language shouldBe LanguageName.ENGLISH
                    difficulty shouldBe GameDifficulty.HARD

                    userId shouldBe authenticatedUser.userInfo.id
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
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - Anonymous user cannot start a words typing game`() {
                val response = wordsTypingGameAPIClient.startGame(
                    body = StartGameRequest(
                        language = LanguageName.ENGLISH,
                        difficulty = GameDifficulty.HARD
                    ),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - User with no words assigned cannot start a words typing game`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val response = wordsTypingGameAPIClient.startGame(
                    body = StartGameRequest(
                        language = LanguageName.ENGLISH,
                        difficulty = GameDifficulty.HARD
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - User with insufficient number of words assigned cannot start a words typing game`() {
                val requiredNumberOfWords = 12

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository,
                    numberOfWordsToLoad = requiredNumberOfWords - 1
                )

                val response = wordsTypingGameAPIClient.startGame(
                    body = StartGameRequest(
                        language = LanguageName.ENGLISH,
                        difficulty = GameDifficulty.HARD
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - User with no proficiency in the language cannot start a words typing game`() {
                val unknownForUserLanguage = LanguageName.ITALIAN

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.A1
                    )
                )

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = wordsTypingGameAPIClient.startGame(
                    body = StartGameRequest(
                        language = unknownForUserLanguage,
                        difficulty = GameDifficulty.HARD
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Cannot start a game without providing difficulty`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.C1
                    )
                )

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = wordsTypingGameAPIClient.post(
                    url = "/api/v1/games/words-typing/start",
                    body = UnsafeStartGameRequestData(
                        language = LanguageName.ENGLISH,
                        difficulty = null
                    ),
                    user = authenticatedUser,
                    responseBodyType = object :
                        ParameterizedTypeReference<StartedWordsTypingGameResponse>() {}
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Cannot start a game without providing language`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.C1
                    )
                )

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = wordsTypingGameAPIClient.post(
                    url = "/api/v1/games/words-typing/start",
                    body = UnsafeStartGameRequestData(
                        language = null,
                        difficulty = GameDifficulty.HARD
                    ),
                    user = authenticatedUser,
                    responseBodyType = object :
                        ParameterizedTypeReference<StartedWordsTypingGameResponse>() {}
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
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

                val a = wordsTypingGameMocker.mockFromJsonSource(
                    userId = authenticatedUser.userInfo.id,
                )

                gameSavedInDb = ongoingGameMapper.toWordsTypingDTO(
                    entity = ongoingGameRepository.findById(a.first.id).block()!!
                )
            }

            @AfterEach
            fun afterEach() {
                userRepository.deleteById(authenticatedUser.userInfo.id).block()
            }

            private fun getPerfectAnswersForQuestions(
                numberOfProperAnswers: Int? = null
            ): Set<WordUserAnswer> {
                return gameSavedInDb.properAnswers.getPerfectAnswersForQuestions(
                    numberOfProperAnswers
                )
            }

            private fun finishWordsTypingGame(
                gameId: UUID = gameSavedInDb.id,
                numberOfProperAnswers: Int? = null,
                answers: Set<WordUserAnswer> = getPerfectAnswersForQuestions(
                    numberOfProperAnswers
                )
            ): FinishedWordsTypingGameResponse {
                val response = wordsTypingGameAPIClient.finishGame(
                    body = FinishWordsTypingGameRequest(
                        gameId = gameId,
                        duration = "00:10:00",
                        answers = answers
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                return response.body!!
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
                response.reviewedAnswers.assertPointsForMistakesWereAssignedProperly(alteredAnswers)
            }

            private fun assertDBPointsWereUpdatedProperly(
                response: FinishedWordsTypingGameResponse,
                alteredAnswers: Set<AlteredWordProperAnswer> = emptySet()
            ) {
                wordRepository.assertDBPointsWereUpdatedProperly(
                    words = gameSavedInDb.properAnswers.values.toSet(),
                    language = gameSavedInDb.language,
                    userId = authenticatedUser.userInfo.id,
                    properAnswers = response.reviewedAnswers,
                    alteredAnswers = alteredAnswers
                )
            }

            @Test
            fun `200 - Ongoing game should be removed and finished game should be created instead`() {
                finishedGameRepository
                    .findAllByUserId(authenticatedUser.userInfo.id)
                    .collectList()
                    .block()!!
                    .shouldHaveSize(0)

                ongoingGameRepository.findById(gameSavedInDb.id).block() shouldNotBe null

                finishWordsTypingGame()

                ongoingGameRepository.findById(gameSavedInDb.id).block() shouldBe null
                finishedGameRepository
                    .findAllByUserId(authenticatedUser.userInfo.id)
                    .collectList()
                    .block()!!
                    .shouldHaveSize(1)
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

                response.score shouldBe GamesConfig.GameScoring.MaxScore.WORDS_TYPING
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
                        WordAnswerScore.INCORRECT to 3,
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
                        WordAnswerScore.HALF_CORRECT to 3,
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
                        WordAnswerScore.INCORRECT to 3,
                    )
                )

                val response = finishWordsTypingGame(
                    answers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Points should be properly assigned - both HALF_CORRECT and INCORRECT`() {
                userRepository.deleteById(authenticatedUser.userInfo.id).block()
                authenticatedUser = mockAuthenticatedUser()

                gameSavedInDb = wordsTypingGameMocker
                    .mockFromJsonSource(
                        userId = authenticatedUser.userInfo.id,
                        difficulty = GameDifficulty.MEDIUM,
                    )
                    .first

                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()
                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        WordAnswerScore.HALF_CORRECT to 3,
                        WordAnswerScore.INCORRECT to 3,
                    )
                )

                val response = finishWordsTypingGame(
                    answers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Words can be marked as completed`() {
                userRepository.deleteById(authenticatedUser.userInfo.id).block()
                authenticatedUser = mockAuthenticatedUser()

                gameSavedInDb = wordsTypingGameMocker
                    .mockFromJsonSource(
                        userId = authenticatedUser.userInfo.id,
                        difficulty = GameDifficulty.MEDIUM,
                    )
                    .first

                wordRepository
                    .saveAll(
                        wordRepository
                            .findAllByUserId(authenticatedUser.userInfo.id)
                            .collectList()
                            .block()!!
                            .map {
                                it.copy(
                                    points = GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD - 1
                                )
                            }
                    )
                    .collectList()
                    .block()!!

                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()
                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        WordAnswerScore.HALF_CORRECT to 3,
                    )
                )

                wordsTypingGameAPIClient.finishGame(
                    body = FinishWordsTypingGameRequest(
                        gameId = gameSavedInDb.id,
                        duration = "00:10:00",
                        answers = alteredAnswers.toRequestBody(perfectAnswers)
                    ),
                    user = authenticatedUser
                )

                val wordsUsedInGame = perfectAnswers.map { it.answer }

                wordRepository
                    .findAllByUserId(authenticatedUser.userInfo.id)
                    .collectList()
                    .block()!!
                    .filter { it.origin in wordsUsedInGame }
                    .forEach {
                        it.points shouldBeGreaterThanOrEqual GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD
                        it.isCompleted shouldBe true
                    }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - Anonymous user cannot finish a words typing game`() {
                val response = wordsTypingGameAPIClient.finishGame(
                    body = FinishWordsTypingGameRequest(
                        gameId = UUID.randomUUID(),
                        duration = "00:10:00",
                        answers = emptySet()
                    ),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - User cannot finish a game that does not belong to them`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val otherUser = userSeeder.seedOneEntity()

                val otherUserDTO = userMapper.toDTO(otherUser)

                val gameId: UUID = wordsTypingGameMocker
                    .mockFromJsonSource(
                        userId = otherUserDTO.id,
                        difficulty = GameDifficulty.MEDIUM,
                    )
                    .first.id

                val response = wordsTypingGameAPIClient.finishGame(
                    body = FinishWordsTypingGameRequest(
                        gameId = gameId,
                        duration = "00:30:00",
                        answers = emptySet()
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - Words typing game cannot be finished with user answer to single word over 255 characters long`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val gameId: UUID = wordsTypingGameMocker
                    .mockFromJsonSource(
                        userId = authenticatedUser.userInfo.id,
                        difficulty = GameDifficulty.MEDIUM,
                    )
                    .first.id

                val response = wordsTypingGameAPIClient.finishGame(
                    body = FinishWordsTypingGameRequest(
                        gameId = gameId,
                        duration = "00:10:00",
                        answers = setOf(
                            WordUserAnswer(
                                id = UUID.randomUUID(),
                                answer = "x".repeat(256)
                            )
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Words typing game cannot be finished with duration not in a proper format`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val gameId: UUID = wordsTypingGameMocker
                    .mockFromJsonSource(
                        userId = authenticatedUser.userInfo.id,
                        difficulty = GameDifficulty.MEDIUM,
                    )
                    .first.id

                val response = wordsTypingGameAPIClient.finishGame(
                    body = FinishWordsTypingGameRequest(
                        gameId = gameId,
                        duration = "not a number",
                        answers = setOf(
                            WordUserAnswer(
                                id = UUID.randomUUID(),
                                answer = "test"
                            )
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}