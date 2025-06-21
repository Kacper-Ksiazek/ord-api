package com.backend.ord.controllers.games

import com.backend.ord.config.GamesConfig
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.games.bases.GameControllerTestBase
import com.backend.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.UserRepository
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.core.word.repository.WordRepository
import com.backend.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.backend.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.enums.GameGrade
import com.backend.ord.features.game.model.ongoing_game.enums.GameType
import com.backend.ord.features.game.repositories.FinishedGameRepository
import com.backend.ord.features.game.repositories.OngoingGameRepository
import com.backend.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.backend.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.backend.ord.features.game.variants.crossword.dto.helpers.board.Coordinates
import com.backend.ord.features.game.variants.crossword.dto.helpers.board.CrosswordWordDirection
import com.backend.ord.features.game.variants.crossword.dto.helpers.question.getCoordinatesOfLetterAtIndex
import com.backend.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.backend.ord.features.game.variants.shared.enums.AnswerScore
import com.backend.ord.features.game.variants.words_typing.dto.api_requests.CrosswordUserAnswers
import com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.backend.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.repository.GameTokensUsageRepository
import com.backend.ord.features.user_activity_log.model.enums.UserActivityType
import com.backend.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.seeders.factories.WordFactory
import com.backend.ord.shared.utils.HIDDEN_CHARACTER
import com.backend.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.backend.ord.testing_utils.dto.AlteredWordProperAnswer
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.backend.ord.testing_utils.dto.toRequestBody
import com.backend.ord.testing_utils.extensions.*
import com.backend.ord.testing_utils.mocks.games.CrosswordGameMocker
import com.backend.ord.testing_utils.mocks.games.GameMockerBase
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
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
@DisplayName("- CrosswordGameController")
class TestCrosswordGameController @Autowired constructor(
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
    val crosswordGameMocker = CrosswordGameMocker(
        objectMapper = objectMapper,
        userMapper = userMapper,
        wordRepository = wordRepository,
        ongoingGameMapper = ongoingGameMapper,
        ongoingGameRepository = ongoingGameRepository,
        wordMockFactory = wordMockFactory,
        mockMvc = mockMvc,
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

            crosswordSavedInDb.user.id shouldBe authenticatedUser.userInfo.id

            crosswordSavedInDb.properAnswers.questions.size shouldBe crosswordSentToUser.instruction.questions.size
            crosswordSavedInDb.properAnswers.finalWord.length shouldBe crosswordSentToUser.instruction.answer.length

            crosswordSavedInDb.properAnswers.questions.entries.forEach { (questionId, answer) ->
                crosswordSentToUser.instruction.questions.find { it.id == questionId }.let {
                    it shouldNotBe null
                    it!!.word.length shouldBe answer.length
                }
            }

            gameTokensUsageRepository.findAllForUser(userId = authenticatedUser.userInfo.id).size shouldBeGreaterThanOrEqualTo 1

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
            fun `Game returned in response is the game saved in the DB`() {
                crosswordSavedInDb.id shouldBe crosswordSentToUser.gameId
            }

            @Test
            fun `Game is properly saved in the DB`() {
                crosswordSavedInDb.type shouldBe GameType.CROSSWORD
                crosswordSavedInDb.language shouldBe GameMockerBase.Companion.DefaultParams.language
                crosswordSavedInDb.difficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty

                crosswordSavedInDb.user.id shouldBe authenticatedUser.userInfo.id
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
            fun `GPT use logs are properly saved in the DB`() {
                val logs = gameTokensUsageRepository.findAllForUser(userId = authenticatedUser.userInfo.id)

                logs shouldNotHaveSize 0

                logs.forEach {
                    it.gameType shouldBe GameType.CROSSWORD
                    it.consumptionType shouldBe GamesGPTTokensConsumptionType.GENERATE
                    it.language shouldBe GameMockerBase.Companion.DefaultParams.language
                    it.gameDifficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty
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

            internal fun GameRequestFactory.startGameRequest(
                authenticatedUser: MockedAuthenticatedUser?,
                language: LanguageName? = LanguageName.ENGLISH,
                difficulty: GameDifficulty? = GameDifficulty.HARD,
            ): MockHttpServletRequestBuilder {
                return startGameRequest(
                    gameType = GameType.CROSSWORD,
                    language = language,
                    difficulty = difficulty,
                    authenticatedUser = authenticatedUser
                )
            }

            @Test
            fun `403 - Anonymous user cannot start a crossword game`() {
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

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser(
                )

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

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

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
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

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
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

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
                crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(authenticatedUser.userInfo).first
            }

            @AfterEach
            fun afterEach() {
                userRepository.deleteById(authenticatedUser.userInfo.id)
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
                val request = gameRequestFactory.finishGameRequest(
                    gameType = GameType.CROSSWORD,
                    authenticatedUser = authenticatedUser,
                    gameId = crosswordSavedInDb.id,
                    answers = CrosswordUserAnswers(
                        finalWord = finalWord,
                        questions = questionsAnswers
                    )
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                return getResponseBody<FinishedCrosswordGameResponse>(response)
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
                response.properQuestionsAnswers.assertPointsForMistakesWereAssignedProperly(alteredAnswers)
            }

            private fun assertDBPointsWereUpdatedProperly(
                response: FinishedCrosswordGameResponse,
                alteredAnswers: Set<AlteredWordProperAnswer> = emptySet()
            ) {
                wordRepository.assertDBPointsWereUpdatedProperly(
                    words = crosswordSavedInDb.properAnswers.questions.values.toSet(),
                    language = crosswordSavedInDb.language,
                    userId = authenticatedUser.userInfo.id,
                    properAnswers = response.properQuestionsAnswers,
                    alteredAnswers = alteredAnswers
                )
            }

            @Test
            fun `200 - Ongoing game should be removed and finished game should be created instead`() {
                finishedGameRepository.findAllForUser(authenticatedUser.userInfo.id).shouldHaveSize(0)
                ongoingGameRepository.findByIdOrNull(crosswordSavedInDb.id) shouldNotBe null

                finishCrosswordGame()

                ongoingGameRepository.findByIdOrNull(crosswordSavedInDb.id) shouldBe null
                finishedGameRepository.findAllForUser(authenticatedUser.userInfo.id).shouldHaveSize(1)
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

                response.finalScore shouldBe 100.0
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
                        AnswerScore.INCORRECT to 3,
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

                response.properFinalWord.expectedAnswer shouldBe crosswordSavedInDb.properAnswers.finalWord
                response.properFinalWord.userAnswer shouldBe alteredFinalAnswer
                response.properFinalWord.score shouldBe AnswerScore.INCORRECT
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
                        AnswerScore.HALF_CORRECT to 3,
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
                        AnswerScore.INCORRECT to 3,
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

                val response = finishCrosswordGame(
                    questionsAnswers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Words can be marked as completed`() {
                crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(
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

                finishCrosswordGame(
                    questionsAnswers = alteredAnswers.toRequestBody(perfectAnswers)
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
                answers: CrosswordUserAnswers = CrosswordUserAnswers(
                    finalWord = "answer",
                    questions = emptySet()
                )
            ): MockHttpServletRequestBuilder {
                return finishGameRequest(
                    gameType = GameType.CROSSWORD,
                    authenticatedUser = authenticatedUser,
                    gameId = gameId,
                    answers = answers
                )
            }

            @Test
            fun `403 - Anonymous user cannot finish a crossword game`() {
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

                val crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(
                    userDTO = userMapper.toDTO(userSeeder.seedOneEntity()),
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val request = gameRequestFactory.finishGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = crosswordSavedInDb.id,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                    it.response
                }
            }

            @Test
            fun `400 - Crossword cannot be finished with user answer to single word over 255 characters long`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val crosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(
                    userDTO = userMapper.toDTO(userSeeder.seedOneEntity()),
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val request = gameRequestFactory.finishGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = crosswordSavedInDb.id,
                    answers = CrosswordUserAnswers(
                        finalWord = "answer",
                        questions = setOf(
                            WordUserAnswer(
                                id = UUID.randomUUID(),
                                answer = "x".repeat(256)
                            )
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