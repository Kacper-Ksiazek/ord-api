package com.backend.ord.controllers.games

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.games.bases.GameControllerTestBase
import com.backend.ord.core.user.UserRepository
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameResult
import com.backend.ord.repositories.*
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.backend.ord.testing_utils.mocks.games.CrosswordGameMocker
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldHaveSize
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

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- GameController")
class TestGameController @Autowired constructor(
    private val userActivityLogRepository: UserActivityLogRepository,
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
    @DisplayName("[DELETE] /api/v1/games/cancel/{gameId} - cancel a crossword game")
    inner class CancelCrosswordGame {
        lateinit var authenticatedUser: MockedAuthenticatedUser
        lateinit var ongoingCrosswordSavedInDb: OngoingCrosswordGameDTO

        @BeforeEach
        fun beforeEach() {
            authenticatedUser = mockAuthenticatedUser()

            ongoingCrosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(authenticatedUser.userInfo).first
        }

        @AfterEach
        fun afterEach() {
            userRepository.deleteById(authenticatedUser.userInfo.id)
        }

        private fun cancelGame(
            authenticatedUser: MockedAuthenticatedUser? = this.authenticatedUser,
            expectedStatus: Int = HttpStatus.NO_CONTENT.value()
        ) {
            mockMvc.perform(
                gameRequestFactory.cancelGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = ongoingCrosswordSavedInDb.id
                )
            ).andReturn().let {
                it.response.status shouldBe expectedStatus
            }
        }


        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            @Test
            fun `200 - Crossword game can be canceled`() {
                cancelGame()

                ongoingGameRepository.findByIdOrNull(ongoingCrosswordSavedInDb.id) shouldBe null
                finishedGameRepository.findAllForUser(authenticatedUser.userInfo.id).first().let {
                    it shouldNotBe null

                    it.result shouldBe GameResult.CANCELLED
                    it.language shouldBe ongoingCrosswordSavedInDb.language
                    it.difficulty shouldBe ongoingCrosswordSavedInDb.difficulty
                }
            }

            @Test
            fun `200 - User activity log should be assigned after canceling the game`() {
                cancelGame()

                val logs = userActivityLogRepository.findAllForUser(authenticatedUser.userInfo.id)
                logs shouldHaveSize 1

                logs.first().let {
                    it.type shouldBe UserActivityType.GAME_QUIT
                    it.language shouldBe ongoingCrosswordSavedInDb.language
                    it.points shouldBe UserActivityType.GAME_QUIT.points
                    it.gameDifficulty shouldBe ongoingCrosswordSavedInDb.difficulty
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Negative {
            @Test
            fun `404 - Game cannot be cancelled twice`() {
                cancelGame()

                cancelGame(
                    expectedStatus = HttpStatus.NOT_FOUND.value()
                )
            }

            @Test
            fun `403 - Unauthorized user cannot cancel a crossword game`() {
                cancelGame(
                    authenticatedUser = null,
                    expectedStatus = HttpStatus.FORBIDDEN.value()
                )

                ongoingGameRepository.findByIdOrNull(ongoingCrosswordSavedInDb.id) shouldNotBe null
            }

            @Test
            fun `404 - User cannot cancel somebody else game`() {
                cancelGame(
                    authenticatedUser = mockAuthenticatedUser(),
                    expectedStatus = HttpStatus.NOT_FOUND.value()
                )

                ongoingGameRepository.findByIdOrNull(ongoingCrosswordSavedInDb.id) shouldNotBe null
            }

        }


    }
}