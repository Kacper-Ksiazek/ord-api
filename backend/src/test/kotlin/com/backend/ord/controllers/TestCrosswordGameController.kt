package com.backend.ord.controllers

import com.backend.ord.api.responses.games.StartedCrosswordGameResponse
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.request_factories.GameRequestFactory
import com.backend.ord.controllers.utils_for_testing.ControllerTestBase
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.application.games.Coordinates
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
import com.backend.ord.repositories.GameRepository
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.repositories.gpt_tokens_usage.GameTokensUsageRepository
import com.backend.ord.repositories.pivots.WordsUsedInGamesRepository
import com.backend.ord.services.ai.dto.crossword.CrosswordWordDirection
import com.backend.ord.services.ai.dto.crossword.getCoordinatesOfLetterAtIndex
import com.backend.ord.utils.games.HIDDEN_CHARACTER
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@DisplayName("- CrosswordGameController")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestCrosswordGameController @Autowired constructor(
    mockMvc: MockMvc?,
    objectMapper: ObjectMapper,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    private val wordRepository: WordRepository,
    private val userMapper: UserMapper,
    private val gameMapper: GameMapper,
    private val gameRepository: GameRepository,
    private val gameTokensUsageRepository: GameTokensUsageRepository,
    private val wordsUsedInGamesRepository: WordsUsedInGamesRepository
) : ControllerTestBase(
    mockMvc = mockMvc!!,
    objectMapper = objectMapper,

    userMapper = userMapper,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
) {
    private val gameRequestFactory = GameRequestFactory(objectMapper)

    @Nested
    @DisplayName("[POST] /api/v1/games/crossword/start - start a new crossword game")
    inner class StartCrosswordGame {
        @RepeatedTest(100)
        @Disabled("This test is disabled for automatic execution. Run manually when needed.")
        fun `Crossword can be properly stared - function encapsulates the entire process`() {
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var crosswordSentToUser: StartedCrosswordGameResponse

            val crosswordLanguage = LanguageName.ENGLISH
            val crosswordDifficulty = GameDifficulty.HARD

            // 1. Create a user
            authenticatedUser = mockAuthenticatedUser(
                languages = mapOf(
                    crosswordLanguage to LanguageProficiencyLevel.C1
                )

            )
            // 2. Assign exactly 12 words to the user (to be used in the crossword game on Hard difficulty)
            loadWordsFromResourceFile(
                user = userMapper.toEntity(authenticatedUser.userInfo),
                wordsRepository = wordRepository
            )
            // 3. Make a request to start a new crossword game
            val request = gameRequestFactory.startGameRequest(
                gameType = GameType.CROSSWORD,
                language = crosswordLanguage,
                difficulty = crosswordDifficulty,
                authenticatedUser = authenticatedUser,
            )
            val response = mockMvc.perform(request).andReturn().let {
                it.response.status shouldBe HttpStatus.OK.value()
                it.response
            }

            // 4. Retrieve the response body
            crosswordSentToUser = getResponseBody<StartedCrosswordGameResponse>(response)

            // 5. Retrieve the crossword game from the database
            var crosswordSavedInDb: CrosswordGameDTO = gameMapper.toCrosswordDTO(
                gameRepository
                    .findAllForUser(authenticatedUser.userInfo.id)
                    .first()
            )

            crosswordSavedInDb.id shouldBe crosswordSentToUser.gameId

            crosswordSavedInDb.grade shouldBe GameGrade.NA
            crosswordSavedInDb.type shouldBe GameType.CROSSWORD
            crosswordSavedInDb.status shouldBe GameStatus.IN_PROGRESS
            crosswordSavedInDb.language shouldBe crosswordLanguage
            crosswordSavedInDb.difficulty shouldBe crosswordDifficulty

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

            val numberOfWordsSavedAsPivotEntities =
                wordsUsedInGamesRepository.findAllByGameId(gameId = crosswordSavedInDb.id).size
            val numberOfWordsUsedInCrosswordGame = crosswordSentToUser.instruction.questions.size

            numberOfWordsSavedAsPivotEntities shouldBe numberOfWordsUsedInCrosswordGame

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
            lateinit var crosswordSavedInDb: CrosswordGameDTO

            val crosswordLanguage = LanguageName.ENGLISH
            val crosswordDifficulty = GameDifficulty.HARD

            @BeforeAll
            fun beforeAll() {
                // 1. Create a user
                authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(
                        crosswordLanguage to LanguageProficiencyLevel.C1
                    )

                )
                // 2. Assign exactly 12 words to the user (to be used in the crossword game on Hard difficulty)
                loadWordsFromResourceFile(
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    wordsRepository = wordRepository
                )
                // 3. Make a request to start a new crossword game
                val request = gameRequestFactory.startGameRequest(
                    gameType = GameType.CROSSWORD,
                    language = crosswordLanguage,
                    difficulty = crosswordDifficulty,
                    authenticatedUser = authenticatedUser,
                )
                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                // 4. Retrieve the response body
                crosswordSentToUser = getResponseBody<StartedCrosswordGameResponse>(response)

                // 5. Retrieve the crossword game from the database
                crosswordSavedInDb = gameMapper.toCrosswordDTO(
                    gameRepository
                        .findAllForUser(authenticatedUser.userInfo.id)
                        .first()
                )
            }

            @Test
            fun `Game returned in response is the game saved in the DB`() {
                crosswordSavedInDb.id shouldBe crosswordSentToUser.gameId
            }

            @Test
            fun `Game is properly saved in the DB`() {
                crosswordSavedInDb.grade shouldBe GameGrade.NA
                crosswordSavedInDb.type shouldBe GameType.CROSSWORD
                crosswordSavedInDb.status shouldBe GameStatus.IN_PROGRESS
                crosswordSavedInDb.language shouldBe crosswordLanguage
                crosswordSavedInDb.difficulty shouldBe crosswordDifficulty

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
            fun `All words used in the crossword game are saved in the DB`() {
                val numberOfWordsSavedAsPivotEntities =
                    wordsUsedInGamesRepository.findAllByGameId(gameId = crosswordSavedInDb.id).size
                val numberOfWordsUsedInCrosswordGame = crosswordSentToUser.instruction.questions.size

                numberOfWordsSavedAsPivotEntities shouldBe numberOfWordsUsedInCrosswordGame
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
            //
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/games/crossword/start - start a new crossword game")
    inner class FinishCrosswordGame {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            //
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            //
        }
    }
}
