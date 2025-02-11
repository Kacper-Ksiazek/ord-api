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
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
import com.backend.ord.repositories.GameRepository
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.services.ai.dto.crossword.CrosswordWordDirection
import com.backend.ord.services.ai.dto.crossword.getCoordinatesOfLetterAtIndex
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.databind.ObjectMapper
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class StartCrosswordGame {

        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class Positive {
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var crosswordSentToUser: StartedCrosswordGameResponse
            lateinit var crosswordSavedInDb: CrosswordGameDTO

            @BeforeAll
            fun beforeAll() {
                // 1. Create a user
                authenticatedUser = mockAuthenticatedUser(
                    languages = setOf(
                        Pair(LanguageName.ENGLISH, LanguageProficiencyLevel.C1)
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
                    authenticatedUser = authenticatedUser
                )
                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                // 4. Retrieve the response body
                crosswordSentToUser = getResponseBody<StartedCrosswordGameResponse>(response)

                // 5. Retrieve the crossword game from the database
                crosswordSavedInDb = gameMapper.toCrosswordDTO(
                    gameRepository.findAllForUser(authenticatedUser.userInfo.id).first()
                )
            }

            @Test
            fun `Game can be successfully started`() {
                true shouldBe true

                crosswordSavedInDb.id shouldBe crosswordSavedInDb.id
            }

            @Test
            fun `All rows should have identical sizes`() {
                crosswordSentToUser.board.map { it.size }.distinct().size shouldBe 1
            }

            @Test
            fun `All words should be unique`() {
                crosswordSavedInDb.properAnswers.questions.values.distinct().size shouldBe crosswordSentToUser.instruction.questions.size
            }

            @Test
            fun `Coordinates should not exceed the board's dimensions`() {
                val board = crosswordSentToUser.board

                val extremeCoordinates = Coordinates(
                    x = crosswordSentToUser.instruction.questions.maxOf { it.position!!.coordinates.end.x },
                    y = crosswordSentToUser.instruction.questions.maxOf { it.position!!.coordinates.end.y }
                )

                extremeCoordinates.x shouldBeLessThan board[0].size
                extremeCoordinates.y shouldBeLessThan board.size
            }

            @Test
            fun `All words should be properly placed on the board`() {
                val board = crosswordSentToUser.board

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

                        crosswordSentToUser.board[coordinates.y][coordinates.x] shouldNotBe null
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
            }

            @Test
            fun `The final word's components should form a valid final word`() {
                val allFinalWordComponents =
                    crosswordSentToUser.instruction.questions.flatMap { it.lettersInAnswer ?: emptyList() }

                crosswordSentToUser.instruction.questions.forEach { question ->
                    val expectedFinalWord = crosswordSentToUser.properAnswers.finalWord

                    val currentQuestionWithoutLettersHidden: String =
                        crosswordSentToUser.properAnswers.questions[question.id]!!

                    question.lettersInAnswer?.forEach { answerComponent ->
                        expectedFinalWord[answerComponent.indexInPassword] shouldBe currentQuestionWithoutLettersHidden[answerComponent.indexInWord]
                    }
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
