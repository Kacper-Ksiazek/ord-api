package com.backend.ord.controllers

import com.backend.ord.api.requests.games.data.CrosswordUserAnswersData
import com.backend.ord.api.requests.games.data.WordUserAnswer
import com.backend.ord.api.responses.games.bases.StartedCrosswordGameResponse
import com.backend.ord.api.responses.games.crossword.FinishedCrosswordGameResponse
import com.backend.ord.config.GamesConfig
import com.backend.ord.controllers.utils_for_testing.AlteredProperAnswer
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.controllers.utils_for_testing.bases.GameControllerTestBase
import com.backend.ord.controllers.utils_for_testing.mockAnswersWithMistakes
import com.backend.ord.controllers.utils_for_testing.toRequestBody
import com.backend.ord.domain.application.games.crossword.board.Coordinates
import com.backend.ord.domain.application.games.crossword.board.CrosswordWordDirection
import com.backend.ord.domain.application.games.crossword.getCoordinatesOfLetterAtIndex
import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.enums.application.game.AnswerScore
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
import com.backend.ord.repositories.UserActivityLogRepository
import com.backend.ord.repositories.gpt_tokens_usage.GameTokensUsageRepository
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.utils.HIDDEN_CHARACTER
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldHaveSize
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
import java.util.*

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- CrosswordGameController")
class TestCrosswordGameController @Autowired constructor(
    objectMapper: ObjectMapper,
    private val gameTokensUsageRepository: GameTokensUsageRepository,
    private val userActivityLogRepository: UserActivityLogRepository,
    private val userSeeder: UserSeeder,
) : GameControllerTestBase(objectMapper) {
    @Nested
    @DisplayName("[POST] /api/v1/games/crossword/start - start a new crossword game")
    inner class StartCrosswordGame {
        @RepeatedTest(100)
        @Disabled("This test is disabled for automatic execution. Run manually when needed.")
        fun `Crossword can be properly stared - function encapsulates the entire process`() {
            val (authenticatedUser, crosswordSentToUser, crosswordSavedInDb) = mockStartedCrosswordGame()

            crosswordSavedInDb.id shouldBe crosswordSentToUser.gameId

            crosswordSavedInDb.type shouldBe GameType.CROSSWORD
            crosswordSavedInDb.language shouldBe CrosswordDefaultValues.language
            crosswordSavedInDb.difficulty shouldBe CrosswordDefaultValues.difficulty

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
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var crosswordSentToUser: StartedCrosswordGameResponse
            lateinit var crosswordSavedInDb: OngoingCrosswordGameDTO

            @BeforeAll
            fun beforeAll() {
                with(mockStartedCrosswordGame()) {
                    authenticatedUser = first
                    crosswordSentToUser = second
                    crosswordSavedInDb = third
                }
            }

            @Test
            fun `Game returned in response is the game saved in the DB`() {
                crosswordSavedInDb.id shouldBe crosswordSentToUser.gameId
            }

            @Test
            fun `Game is properly saved in the DB`() {
                crosswordSavedInDb.type shouldBe GameType.CROSSWORD
                crosswordSavedInDb.language shouldBe CrosswordDefaultValues.language
                crosswordSavedInDb.difficulty shouldBe CrosswordDefaultValues.difficulty

                crosswordSavedInDb.user.id shouldBe authenticatedUser.userInfo.id
            }

            @Test
            fun `Proper answers are properly saved in the DB`() {
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
                gameTokensUsageRepository.findAllForUser(userId = authenticatedUser.userInfo.id).size shouldBeGreaterThanOrEqualTo 1
            }

            @Test
            fun `All rows should have identical sizes`() {
                crosswordSentToUser.instruction.board.map { it.size }.distinct().size shouldBe 1
            }

            @Test
            fun `All words should be unique`() {
                crosswordSavedInDb.properAnswers.questions.values.distinct().size shouldBe crosswordSentToUser.instruction.questions.size
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
            fun `403 - Anonymous user cannot start a crossword game`() {
                val request = gameRequestFactory.startGameRequest(
                    gameType = GameType.CROSSWORD,
                    language = LanguageName.ENGLISH,
                    difficulty = GameDifficulty.HARD,
                    authenticatedUser = null,
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                    it.response
                }
            }

            @Test
            fun `400 - User with no words assigned cannot start a crossword game`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.C1
                    )
                )

                val request = gameRequestFactory.startGameRequest(
                    gameType = GameType.CROSSWORD,
                    language = LanguageName.ENGLISH,
                    difficulty = GameDifficulty.HARD,
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
                    languages = mapOf(
                        LanguageName.ENGLISH to LanguageProficiencyLevel.C1
                    )
                )

                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository,
                    numberOfWordsToLoad = requiredNumberOfWords - 1
                )

                val request = gameRequestFactory.startGameRequest(
                    gameType = GameType.CROSSWORD,
                    language = LanguageName.ENGLISH,
                    difficulty = GameDifficulty.HARD,
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
                    gameType = GameType.CROSSWORD,
                    language = unknownForUserLanguage,
                    difficulty = GameDifficulty.HARD,
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
                    gameType = GameType.CROSSWORD,
                    language = LanguageName.ENGLISH,
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
                    gameType = GameType.CROSSWORD,
                    language = null,
                    difficulty = GameDifficulty.HARD,
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
    @DisplayName("[POST] /api/v1/games/crossword/finish - start a new crossword game")
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
                prepareCrosswordGame().let {
                    authenticatedUser = it.first
                    crosswordSavedInDb = it.second
                }
            }

            @AfterEach
            fun afterEach() {
                userRepository.deleteById(authenticatedUser.userInfo.id)
            }

            private fun getPerfectAnswersForQuestions(
                numberOfProperAnswers: Int? = null
            ): Set<WordUserAnswer> {
                val limit = numberOfProperAnswers ?: crosswordSavedInDb.properAnswers.questions.size

                return crosswordSavedInDb.properAnswers.questions.entries.mapIndexed { index, (questionId, answer) ->
                    WordUserAnswer(
                        id = questionId,
                        word = if (index < limit) answer else "__invalid__"
                    )
                }.toSet()
            }

            private fun finishCrosswordGame(
                numberOfProperAnswers: Int? = null,
                finalWord: String = crosswordSavedInDb.properAnswers.finalWord,
                questionsAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions(
                    numberOfProperAnswers
                )
            ): FinishedCrosswordGameResponse {
                val request = gameRequestFactory.finishCrosswordGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = crosswordSavedInDb.id,
                    userAnswers = CrosswordUserAnswersData(
                        answer = finalWord,
                        questionsAnswers = questionsAnswers
                    )
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                return getResponseBody<FinishedCrosswordGameResponse>(response)
            }

            private fun assertUserActivityLogForCompletingCrossword(expectedType: UserActivityType) {
                val logs = userActivityLogRepository.findAllForUser(authenticatedUser.userInfo.id)
                logs shouldHaveSize 1

                logs.first().let {
                    it.type shouldBe expectedType
                    it.language shouldBe crosswordSavedInDb.language
                    it.points shouldBe expectedType.points
                    it.gameDifficulty shouldBe crosswordSavedInDb.difficulty
                }
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
                var perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
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

            // Prepare test for points calculation
            @Test
            fun `200 - Points should be properly assigned - CORRECT`() {
                val response: FinishedCrosswordGameResponse = finishCrosswordGame()

                assertDBPointsWereUpdatedProperly(response)
            }

            @Test
            fun `200 - Points should be properly assigned - HALF_CORRECT`() {
                prepareCrosswordGame(difficulty = GameDifficulty.MEDIUM)

                var perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
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
                var perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
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
                prepareCrosswordGame(difficulty = GameDifficulty.MEDIUM)

                var perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
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
                prepareCrosswordGame(difficulty = GameDifficulty.MEDIUM)

                wordRepository.saveAll(
                    wordRepository.findAllForUser(authenticatedUser.userInfo.id).map {
                        it.copy(
                            points = GamesConfig.Points.COMPLETE_WORD_THRESHOLD - 1
                        )
                    }
                )

                var perfectAnswers: Set<WordUserAnswer> = getPerfectAnswersForQuestions()

                val alteredAnswers: Set<AlteredProperAnswer> = perfectAnswers.mockAnswersWithMistakes(
                    mistakes = mapOf(
                        AnswerScore.HALF_CORRECT to 3,
                    )
                )

                finishCrosswordGame(
                    questionsAnswers = alteredAnswers.toRequestBody(perfectAnswers)
                )

                wordRepository.findAllForUser(authenticatedUser.userInfo.id).forEach {
                    if (alteredAnswers.find { alteredAnswer -> alteredAnswer.originalAnswer == it.origin } != null) {
                        return@forEach
                    }

                    it.points shouldBeGreaterThanOrEqual GamesConfig.Points.COMPLETE_WORD_THRESHOLD
                    it.isCompleted shouldBe true
                }
            }

            private fun assertPointsForMistakesWereAssignedProperly(
                response: FinishedCrosswordGameResponse,
                alteredAnswers: Set<AlteredProperAnswer>
            ) {
                response.properQuestionsAnswers.forEach { properAnswer ->
                    val alteredAnswer = alteredAnswers.find { it.questionId == properAnswer.id }
                    if (alteredAnswer == null) return@forEach

                    properAnswer.expectedAnswer shouldBe alteredAnswer.originalAnswer
                    properAnswer.score shouldBe alteredAnswer.desiredScore
                }
            }

            private fun assertDBPointsWereUpdatedProperly(
                response: FinishedCrosswordGameResponse,
                alteredAnswers: Set<AlteredProperAnswer> = emptySet()
            ) {
                val wordsUsedInGame = wordRepository.findAllWordByTheirOrigins(
                    origins = crosswordSavedInDb.properAnswers.questions.values.toSet(),
                    language = crosswordSavedInDb.language,
                    userId = authenticatedUser.userInfo.id
                )

                wordsUsedInGame shouldHaveSize crosswordSavedInDb.properAnswers.questions.size

                response.properQuestionsAnswers.forEach {
                    val correspondingWordEntity =
                        wordsUsedInGame.find { word -> word.origin.lowercase() == it.expectedAnswer.lowercase() }
                    val correspondingAlteredAnswer: AlteredProperAnswer? = alteredAnswers.find { alteredAnswer ->
                        alteredAnswer.questionId == it.id
                    }

                    correspondingWordEntity!!.points shouldBe it.score.dbPoints

                    if (correspondingAlteredAnswer != null) {
                        correspondingAlteredAnswer.desiredScore shouldBe it.score
                    }
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `403 - Anonymous user cannot finish a crossword game`() {
                val request = gameRequestFactory.finishCrosswordGameRequest(
                    authenticatedUser = null,
                    gameId = UUID.randomUUID(),
                    userAnswers = CrosswordUserAnswersData(
                        answer = "answer",
                        questionsAnswers = emptySet()
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                    it.response
                }
            }

            @Test
            fun `404 - User cannot finish a game that does not belong to them`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val crosswordSavedInDb = mockCrosswordGames.seedFromJSONFile(
                    user = userSeeder.seedOneEntity()
                ).random().first

                val request = gameRequestFactory.finishCrosswordGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = crosswordSavedInDb.id,
                    userAnswers = CrosswordUserAnswersData(
                        answer = "answer",
                        questionsAnswers = emptySet()
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                    it.response
                }
            }

            @Test
            fun `400 - Crossword cannot be finished with user answer to single word over 255 characters long`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val crosswordSavedInDb = mockCrosswordGames.seedFromJSONFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo)
                ).random().first

                val request = gameRequestFactory.finishCrosswordGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = crosswordSavedInDb.id,
                    userAnswers = CrosswordUserAnswersData(
                        answer = "answer",
                        questionsAnswers = setOf(
                            WordUserAnswer(
                                id = UUID.randomUUID(),
                                word = "x".repeat(256)
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


    private object CrosswordDefaultValues {
        val language = LanguageName.ENGLISH
        val difficulty = GameDifficulty.HARD
    }

    private fun mockAuthenticatedUser(): MockedAuthenticatedUser {
        return mockAuthenticatedUser(
            languages = mapOf(
                CrosswordDefaultValues.language to LanguageProficiencyLevel.C1
            )
        )
    }

    private fun mockStartedCrosswordGame(): Triple<
            MockedAuthenticatedUser,
            StartedCrosswordGameResponse,
            OngoingCrosswordGameDTO
            > {
        // 1. Create a user
        val authenticatedUser = mockAuthenticatedUser()

        // 2. Assign exactly 12 words to the user (to be used in the crossword game on Hard difficulty)
        loadWordsFromResourceFile(
            user = userMapper.toEntity(authenticatedUser.userInfo),
            wordsRepository = wordRepository
        )
        // 3. Make a request to start a new crossword game
        val request = gameRequestFactory.startGameRequest(
            gameType = GameType.CROSSWORD,
            language = CrosswordDefaultValues.language,
            difficulty = CrosswordDefaultValues.difficulty,
            authenticatedUser = authenticatedUser,
        )
        val response = mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.OK.value()
            it.response
        }

        // 4. Retrieve the response body
        val crosswordSentToUser = getResponseBody<StartedCrosswordGameResponse>(response)

        // 5. Retrieve the crossword game from the database
        val crosswordSavedInDb = ongoingGameMapper.toCrosswordDTO(
            ongoingGameRepository
                .findAllForUser(authenticatedUser.userInfo.id)
                .first()
        )

        return Triple(
            authenticatedUser,
            crosswordSentToUser,
            crosswordSavedInDb
        )
    }

}
