package com.backend.ord.controllers

import com.backend.ord.api.responses.games.StartedCrosswordGameResponse
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.request_factories.GameRequestFactory
import com.backend.ord.controllers.utils_for_testing.ControllerTestBase
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
import com.backend.ord.repositories.GameRepository
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
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

            @Nested
            @DisplayName("All words should be properly placed on the board")
            inner class AllWordsShouldBeProperlyPlacedOnTheBoard {
                @Test
                fun `All words should fit on the board`() {
                    val board = crosswordSentToUser.board

                    crosswordSentToUser.instruction.questions.forEach {
                        it.coordinates.end.x shouldBeLessThan board.size
                        it.coordinates.end.y shouldBeLessThan board[0].size
                    }
                }

                @Test
                fun `First letter of each word should be revealed`() {
                    val board = crosswordSentToUser.board

                    crosswordSentToUser.instruction.questions.forEach {
                        board[it.coordinates.start.x][it.coordinates.start.y] shouldBe it.word[0]
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
