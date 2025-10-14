package com.ord.controllers.games

import com.ord.config.GamesConfig
import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameGrade
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.crossword.dto.api_requests.CrosswordUserAnswers
import com.ord.features.game.variants.crossword.dto.api_requests.FinishCrosswordGameRequest
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.helpers.board.Coordinates
import com.ord.features.game.variants.crossword.dto.helpers.board.CrosswordWordDirection
import com.ord.features.game.variants.crossword.dto.helpers.question.getCoordinatesOfLetterAtIndex
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_requests.UnsafeStartGameRequestData
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.WordFactory
import com.ord.shared.utils.HIDDEN_CHARACTER
import com.ord.testing_utils.api.clients.games.CrosswordGameAPIClient
import com.ord.testing_utils.dto.AlteredWordProperAnswer
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.dto.toRequestBody
import com.ord.testing_utils.extensions.*
import com.ord.testing_utils.mocks.games.CrosswordGameMocker
import com.ord.testing_utils.mocks.games.GameMockerBase
import com.ord.utils.resource_readers.loadWordsFromResourceFile
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@DisplayName("- CrosswordGameController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000") // 3 minutes
class TestCrosswordGameController @Autowired constructor(
    private val userActivityLogRepository: UserActivityLogRepository,
    private val userSeeder: UserSeeder,
    private val wordMockFactory: WordFactory,
    private val wordRepository: WordRepository,
    private val ongoingGameMapper: OngoingGameMapper,
    private val ongoingGameRepository: OngoingGameRepository,
    private val finishedGameRepository: FinishedGameRepository,
    userRepository: UserRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder
) {
    private val crosswordGameAPIClient = CrosswordGameAPIClient(webClient)

    private val crosswordGameMocker = CrosswordGameMocker(
        apiClient = crosswordGameAPIClient,
        ongoingGameMapper = ongoingGameMapper,
        ongoingGameRepository = ongoingGameRepository,
        wordMockFactory = wordMockFactory,
        wordRepository = wordRepository
    )

    @Nested
    @DisplayName("[POST] /api/v1/games/crossword/start - start a new crossword game")
    inner class StartCrosswordGame {
        @RepeatedTest(100)
        @Disabled("This test is disabled for automatic execution. Run manually when needed.")
        fun `Crossword can be properly stared - function encapsulates the entire process`() {
            val authenticatedUser = mockAuthenticatedUser()
            val (crosswordSavedInDb, crosswordSentToUser) = crosswordGameMocker.mockThroughApiFlow(authenticatedUser)

            crosswordSavedInDb.id shouldBe crosswordSentToUser.gameId

            crosswordSavedInDb.type shouldBe GameType.CROSSWORD
            crosswordSavedInDb.language shouldBe GameMockerBase.Companion.DefaultParams.language
            crosswordSavedInDb.difficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty

            crosswordSavedInDb.userId shouldBe authenticatedUser.userInfo.id

            crosswordSavedInDb.properAnswers.questions.size shouldBe crosswordSentToUser.instruction.questions.size
            crosswordSavedInDb.properAnswers.finalWord.length shouldBe crosswordSentToUser.instruction.answer.length

            crosswordSavedInDb.properAnswers.questions.entries.forEach { (questionId, answer) ->
                crosswordSentToUser.instruction.questions.find { it.id == questionId }.let {
                    it shouldNotBe null
                    it!!.word.length shouldBe answer.length
                }
            }

            crosswordSentToUser.instruction.board.map { it.size }.distinct().size shouldBe 1

            crosswordSavedInDb.properAnswers.questions.values.distinct().size shouldBe crosswordSentToUser.instruction.questions.size

            val board = crosswordSentToUser.instruction.board

            val extremeCoordinates = Coordinates(
                x = crosswordSentToUser.instruction.questions.maxOf { it.position!!.coordinates.end.x },
                y = crosswordSentToUser.instruction.questions.maxOf { it.position!!.coordinates.end.y }
            )

            extremeCoordinates.x shouldBeLessThan board[0].size
            extremeCoordinates.y shouldBeLessThan board.size

            crosswordSentToUser.instruction.questions.forEach { question ->
                val wordSize = question.word.length

                val coordinates = question.position!!.coordinates

                if (question.position!!.direction === CrosswordWordDirection.HORIZONTAL) {
                    for (i in 0 until wordSize) {
                        board[coordinates.start.y][coordinates.start.x + i] shouldNotBe null
                    }
                } else {
                    for (i in 0 until wordSize) {
                        board[coordinates.start.y + i][coordinates.start.x] shouldNotBe null
                    }
                }
            }

            crosswordSentToUser.instruction.questions.forEach { question ->
                question.lettersInAnswer?.forEach { answerComponent ->
                    val coordinates = question.getCoordinatesOfLetterAtIndex(answerComponent.indexInWord)

                    crosswordSentToUser.instruction.board[coordinates.y][coordinates.x] shouldNotBe null
                }
            }

            crosswordSentToUser.instruction.answer.forEachIndexed { index, letter ->
                if (letter != '*') return@forEachIndexed

                crosswordSentToUser.instruction.questions.find { question ->
                    question.lettersInAnswer?.find { answerComponent ->
                        answerComponent.indexInPassword == index
                    } != null
                } shouldNotBe null
            }

            val numberOfHiddenLettersInFinalWord: Int = crosswordSentToUser.instruction.answer.count { it == '*' }

            val numberOfFinalWordComponents: Int =
                crosswordSentToUser.instruction.questions.sumOf { it.lettersInAnswer?.size ?: 0 }

            numberOfHiddenLettersInFinalWord shouldBe numberOfFinalWordComponents

            crosswordSentToUser.instruction.answer.forEachIndexed { index, letter ->
                if (letter != '*') return@forEachIndexed

                crosswordSentToUser.instruction.questions.find { question ->
                    question.lettersInAnswer?.find { answerComponent ->
                        answerComponent.indexInPassword == index
                    } != null
                } shouldNotBe null
            }

            crosswordSentToUser.instruction.questions.forEach { question ->
                val expectedFinalWord = crosswordSentToUser.properAnswers.finalWord

                val currentQuestionWithoutLettersHidden: String =
                    crosswordSentToUser.properAnswers.questions[question.id]!!

                question.lettersInAnswer?.forEach { answerComponent ->
                    expectedFinalWord[answerComponent.indexInPassword] shouldBe currentQuestionWithoutLettersHidden[answerComponent.indexInWord]
                }
            }

            val revealedLetters = crosswordSentToUser.instruction.answer.mapIndexedNotNull { index, letter ->
                if (letter != HIDDEN_CHARACTER) index else null
            }.toSet()

            revealedLetters.forEach { revealedLetterIndex ->
                crosswordSentToUser.instruction.questions.find { question ->
                    question.lettersInAnswer?.find { answerComponent ->
                        answerComponent.indexInPassword == revealedLetterIndex
                    } != null
                } shouldBe null
            }
        }

        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            val authenticatedUser = mockAuthenticatedUser()

            lateinit var crosswordSavedInDb: OngoingCrosswordGameDTO
            lateinit var crosswordSentToUser: StartedCrosswordGameResponse

            @BeforeAll
            fun beforeAll() {
                with(crosswordGameMocker.mockThroughApiFlow(authenticatedUser)) {
                    crosswordSavedInDb = first
                    crosswordSentToUser = second
                }
            }

            @Test
            fun `Game is properly saved in the DB`() {
                crosswordSavedInDb.id shouldBe crosswordSentToUser.gameId

                crosswordSavedInDb.type shouldBe GameType.CROSSWORD
                crosswordSavedInDb.language shouldBe GameMockerBase.Companion.DefaultParams.language
                crosswordSavedInDb.difficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty

                crosswordSavedInDb.userId shouldBe authenticatedUser.userInfo.id
            }

            @Test
            fun `Proper answers are correctly saved in the DB`() {
                crosswordSavedInDb.properAnswers.questions.size shouldBe crosswordSentToUser.instruction.questions.size
                crosswordSavedInDb.properAnswers.finalWord.length shouldBe crosswordSentToUser.instruction.answer.length

                crosswordSavedInDb.properAnswers.questions.entries.forEach { (questionId, answer) ->
                    crosswordSentToUser.instruction.questions.find { it.id == questionId }.let {
                        it shouldNotBe null
                        it!!.word.length shouldBe answer.length
                    }
                }
            }

            @Test
            fun `All rows should have identical sizes`() {
                crosswordSentToUser.instruction.board.map { it.size }.distinct().size shouldBe 1
            }

            @Test
            fun `All words should be unique`() {
                with(crosswordSavedInDb.properAnswers.questions.values) {
                    this.distinct().size shouldBe this.size
                }
            }

            @Test
            fun `Coordinates should not exceed the board's dimensions`() {
                val board = crosswordSentToUser.instruction.board

                val extremeCoordinates = Coordinates(
                    x = crosswordSentToUser.instruction.questions.maxOf { it.position!!.coordinates.end.x },
                    y = crosswordSentToUser.instruction.questions.maxOf { it.position!!.coordinates.end.y }
                )

                extremeCoordinates.x shouldBeLessThan board[0].size
                extremeCoordinates.y shouldBeLessThan board.size
            }

            @Test
            fun `All words should be properly placed on the board`() {
                val board = crosswordSentToUser.instruction.board

                crosswordSentToUser.instruction.questions.forEach { question ->
                    val wordSize = question.word.length

                    val coordinates = question.position!!.coordinates

                    if (question.position!!.direction === CrosswordWordDirection.HORIZONTAL) {
                        for (i in 0 until wordSize) {
                            board[coordinates.start.y][coordinates.start.x + i] shouldNotBe null
                        }
                    } else {
                        for (i in 0 until wordSize) {
                            board[coordinates.start.y + i][coordinates.start.x] shouldNotBe null
                        }
                    }
                }
            }

            @Test
            fun `All final word components should point to words on the board`() {
                crosswordSentToUser.instruction.questions.forEach { question ->
                    question.lettersInAnswer?.forEach { answerComponent ->
                        val coordinates = question.getCoordinatesOfLetterAtIndex(answerComponent.indexInWord)

                        crosswordSentToUser.instruction.board[coordinates.y][coordinates.x] shouldNotBe null
                    }
                }
            }

            @Test
            fun `The entire final word can be split into components`() {
                crosswordSentToUser.instruction.answer.forEachIndexed { index, letter ->
                    if (letter != '*') return@forEachIndexed

                    crosswordSentToUser.instruction.questions.find { question ->
                        question.lettersInAnswer?.find { answerComponent ->
                            answerComponent.indexInPassword == index
                        } != null
                    } shouldNotBe null
                }
            }

            @Test
            fun `Every hidden letter on final word should have a corresponding component`() {
                val numberOfHiddenLettersInFinalWord: Int =
                    crosswordSentToUser.instruction.answer.count { it == '*' }

                val numberOfFinalWordComponents: Int =
                    crosswordSentToUser.instruction.questions.sumOf { it.lettersInAnswer?.size ?: 0 }

                numberOfHiddenLettersInFinalWord shouldBe numberOfFinalWordComponents

                crosswordSentToUser.instruction.answer.forEachIndexed { index, letter ->
                    if (letter != '*') return@forEachIndexed

                    crosswordSentToUser.instruction.questions.find { question ->
                        question.lettersInAnswer?.find { answerComponent ->
                            answerComponent.indexInPassword == index
                        } != null
                    } shouldNotBe null
                }
            }

            @Test
            fun `The final word's components should form a valid final word`() {
                crosswordSentToUser.instruction.questions.forEach { question ->
                    val expectedFinalWord = crosswordSentToUser.properAnswers.finalWord

                    val currentQuestionWithoutLettersHidden: String =
                        crosswordSentToUser.properAnswers.questions[question.id]!!

                    question.lettersInAnswer?.forEach { answerComponent ->
                        expectedFinalWord[answerComponent.indexInPassword] shouldBe currentQuestionWithoutLettersHidden[answerComponent.indexInWord]
                    }
                }
            }

            @Test
            fun `Revealed letters in final answer should not have corresponding components`() {
                val revealedLetters = crosswordSentToUser.instruction.answer.mapIndexedNotNull { index, letter ->
                    if (letter != HIDDEN_CHARACTER) index else null
                }.toSet()

                revealedLetters.forEach { revealedLetterIndex ->
                    crosswordSentToUser.instruction.questions.find { question ->
                        question.lettersInAnswer?.find { answerComponent ->
                            answerComponent.indexInPassword == revealedLetterIndex
                        } != null
                    } shouldBe null
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - Anonymous user cannot start a crossword game`() {
                val response = crosswordGameAPIClient.startGame(
                    body = StartGameRequest(language = LanguageName.ENGLISH, difficulty = GameDifficulty.HARD),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - User with no words assigned cannot start a crossword game`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val response = crosswordGameAPIClient.startGame(
                    body = StartGameRequest(language = LanguageName.ENGLISH, difficulty = GameDifficulty.HARD),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - User with insufficient number of words assigned cannot start a crossword game`() {
                val requiredNumberOfWords = 12

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository,
                    numberOfWordsToLoad = requiredNumberOfWords - 1
                )

                val response = crosswordGameAPIClient.startGame(
                    body = StartGameRequest(language = LanguageName.ENGLISH, difficulty = GameDifficulty.HARD),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - User with no proficiency in the language cannot start a crossword game`() {
                val unknownForUserLanguage = LanguageName.ITALIAN

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = crosswordGameAPIClient.startGame(
                    body = StartGameRequest(language = unknownForUserLanguage, difficulty = GameDifficulty.HARD),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Cannot start a game without providing difficulty`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = crosswordGameAPIClient.startGame(
                    body = UnsafeStartGameRequestData(language = LanguageName.ENGLISH, difficulty = null),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Cannot start a game without providing language`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = crosswordGameAPIClient.startGame(
                    body = UnsafeStartGameRequestData(language = null, difficulty = GameDifficulty.HARD),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/games/crossword/finish - finish an ongoing crossword game")
    inner class FinishCrosswordGame {

        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var crosswordSavedInDb: OngoingCrosswordGameDTO

            @BeforeEach
            fun beforeEach() {
                authenticatedUser = mockAuthenticatedUser()
                crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(authenticatedUser.userInfo.id).first
            }

            @AfterEach
            fun afterEach() {
                userRepository.deleteById(authenticatedUser.userInfo.id).block()
            }

            private fun getPerfectAnswersForQuestions(
                numberOfProperAnswers: Int? = null
            ): Set<WordUserAnswer> {
                return crosswordSavedInDb.properAnswers.questions.getPerfectAnswersForQuestions(
                    numberOfProperAnswers
                )
            }

            private fun finishCrosswordGame(
                numberOfProperAnswers: Int? = null,
                finalWord: String = crosswordSavedInDb.properAnswers.finalWord,
                questionsAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions(
                    numberOfProperAnswers
                )
            ): FinishedCrosswordGameResponse {
                val response = crosswordGameAPIClient.finishGame(
                    body = FinishCrosswordGameRequest(
                        gameId = crosswordSavedInDb.id,
                        duration = "00:10:00",
                        answers = CrosswordUserAnswers(
                            finalWord = finalWord,
                            questions = questionsAnswers
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                return response.body!!
            }

            private fun assertUserActivityLogForCompletingCrossword(expectedType: UserActivityType) {
                userActivityLogRepository.assertUserActivityLogForCompletingGame(
                    expectedType = expectedType,

                    userId = authenticatedUser.userInfo.id,
                    language = crosswordSavedInDb.language,
                    difficulty = crosswordSavedInDb.difficulty
                )
            }

            private fun assertPointsForMistakesWereAssignedProperly(
                response: FinishedCrosswordGameResponse,
                alteredAnswers: Set<AlteredWordProperAnswer>
            ) {
                response.reviewedAnswers.questions.assertPointsForMistakesWereAssignedProperly(alteredAnswers)
            }

            private fun assertDBPointsWereUpdatedProperly(
                response: FinishedCrosswordGameResponse,
                alteredAnswers: Set<AlteredWordProperAnswer> = emptySet()
            ) {
                wordRepository.assertDBPointsWereUpdatedProperly(
                    words = crosswordSavedInDb.properAnswers.questions.values.toSet(),
                    language = crosswordSavedInDb.language,
                    userId = authenticatedUser.userInfo.id,
                    properAnswers = response.reviewedAnswers.questions,
                    alteredAnswers = alteredAnswers
                )
            }

            @Test
            fun `200 - Ongoing game should be removed and finished game should be created instead`() {
                finishedGameRepository.findAllByUserId(authenticatedUser.userInfo.id).collectList().block()!!
                    .shouldHaveSize(0)
                ongoingGameRepository.findById(crosswordSavedInDb.id).block() shouldNotBe null

                finishCrosswordGame()

                ongoingGameRepository.findById(crosswordSavedInDb.id).block() shouldBe null
                finishedGameRepository.findAllByUserId(authenticatedUser.userInfo.id).collectList().block()!!
                    .shouldHaveSize(1)
            }

            @Test
            fun `200 - Proper user activity log should be assigned after completing the game flawlessly`() {
                finishCrosswordGame()

                assertUserActivityLogForCompletingCrossword(UserActivityType.CROSSWORD_GAME_COMPLETED_FLAWLESSLY)
            }

            @Test
            fun `200 - Proper user activity log should be assigned after completing the game with mistakes`() {
                finishCrosswordGame(
                    numberOfProperAnswers = 9
                )

                assertUserActivityLogForCompletingCrossword(UserActivityType.CROSSWORD_GAME_COMPLETED_WITH_MISTAKES)
            }

            @Test
            fun `200 - Grade can be achieved - S`() {
                val response = finishCrosswordGame()

                response.score shouldBe GamesConfig.GameScoring.MaxScore.CROSSWORD
                response.grade shouldBe GameGrade.S
            }

            @Test
            fun `200 - Grade can be achieved - A`() {
                val response = finishCrosswordGame(
                    numberOfProperAnswers = 11
                )

                response.grade shouldBe GameGrade.A
            }

            @Test
            fun `200 - Grade can be achieved - B`() {
                val response = finishCrosswordGame(
                    numberOfProperAnswers = 9
                )

                response.grade shouldBe GameGrade.B
            }

            @Test
            fun `200 - Grade can be achieved - C`() {
                val response = finishCrosswordGame(
                    numberOfProperAnswers = 7
                )

                response.grade shouldBe GameGrade.C
            }

            @Test
            fun `200 - Grade can be achieved - D`() {
                val response = finishCrosswordGame(
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

                val response = finishCrosswordGame(
                    questionsAnswers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertPointsForMistakesWereAssignedProperly(response, alteredAnswers)
            }

            @Test
            fun `200 - Mistakes in the final answer should be corrected properly`() {
                val alteredFinalAnswer = "altered_final_answer"

                val response = finishCrosswordGame(
                    finalWord = alteredFinalAnswer
                )

                response.reviewedAnswers.finalWord.expectedAnswer shouldBe crosswordSavedInDb.properAnswers.finalWord
                response.reviewedAnswers.finalWord.userAnswer shouldBe alteredFinalAnswer
                response.reviewedAnswers.finalWord.score shouldBe WordAnswerScore.INCORRECT
            }

            @Test
            fun `200 - Points should be properly assigned - CORRECT`() {
                val response: FinishedCrosswordGameResponse = finishCrosswordGame()

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

                val body = alteredAnswers.toRequestBody(perfectAnswers)

                val response = finishCrosswordGame(
                    questionsAnswers = body
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

                val response = finishCrosswordGame(
                    questionsAnswers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Points should be properly assigned - both HALF_CORRECT and INCORRECT`() {
                crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(
                    userId = authenticatedUser.userInfo.id,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        WordAnswerScore.HALF_CORRECT to 3,
                        WordAnswerScore.INCORRECT to 3,
                    )
                )

                val response = finishCrosswordGame(
                    questionsAnswers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Words can be marked as completed`() {
                crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(
                    userId = authenticatedUser.userInfo.id,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                wordRepository.findAllByUserId(authenticatedUser.userInfo.id).collectList().block()!!.let { words ->
                    wordRepository.saveAll(
                        words.map {
                            it.copy(
                                points = GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD - 1
                            )
                        }
                    ).collectList().block()
                }

                val perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredWordProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        WordAnswerScore.HALF_CORRECT to 3,
                    )
                )

                finishCrosswordGame(
                    questionsAnswers = alteredAnswers.toRequestBody(perfectAnswers)
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
            fun `401 - Anonymous user cannot finish a crossword game`() {
                val response = crosswordGameAPIClient.finishGame(
                    body = FinishCrosswordGameRequest(
                        gameId = UUID.randomUUID(),
                        duration = "00:10:00",
                        answers = CrosswordUserAnswers(
                            finalWord = "answer",
                            questions = emptySet()
                        )
                    ),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - User cannot finish a game that does not belong to them`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val otherUserId = userSeeder.seedOneEntity().id
                val crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(
                    userId = otherUserId!!,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val response = crosswordGameAPIClient.finishGame(
                    body = FinishCrosswordGameRequest(
                        gameId = crosswordSavedInDb.id,
                        duration = "00:10:00",
                        answers = CrosswordUserAnswers(
                            finalWord = "answer",
                            questions = emptySet()
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - Crossword cannot be finished with user answer to single word over 255 characters long`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val otherUserId = userSeeder.seedOneEntity().id!!
                val crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(
                    userId = otherUserId,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val response = crosswordGameAPIClient.finishGame(
                    body = FinishCrosswordGameRequest(
                        gameId = crosswordSavedInDb.id,
                        duration = "00:10:00",
                        answers = CrosswordUserAnswers(
                            finalWord = "answer",
                            questions = setOf(
                                WordUserAnswer(
                                    id = UUID.randomUUID(),
                                    answer = "x".repeat(256)
                                )
                            )
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Crossword cannot be finished with duration not in a proper format`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val otherUserId = userSeeder.seedOneEntity().id
                val crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(
                    userId = otherUserId!!,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val response = crosswordGameAPIClient.finishGame(
                    body = FinishCrosswordGameRequest(
                        gameId = crosswordSavedInDb.id,
                        duration = "invalid_duration_format",
                        answers = CrosswordUserAnswers(
                            finalWord = "answer",
                            questions = emptySet()
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}