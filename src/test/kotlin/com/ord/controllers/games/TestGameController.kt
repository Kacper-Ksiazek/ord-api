package com.ord.controllers.games

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.enums.GameResult
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api.clients.games.CrosswordGameAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.mocks.games.CrosswordGameMocker
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- GameController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000") // 3 minutes
class TestGameController @Autowired constructor(
    private val userActivityLogRepository: UserActivityLogRepository,
    private val wordMockFactory: WordFactory,
    private val userRepository: UserRepository,
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
    private val crosswordGameAPIClient = CrosswordGameAPIClient(webClient)

    private val crosswordGameMocker = CrosswordGameMocker(
        apiClient = crosswordGameAPIClient,
        ongoingGameMapper = ongoingGameMapper,
        ongoingGameRepository = ongoingGameRepository,
        wordMockFactory = wordMockFactory,
        wordRepository = wordRepository
    )

    @Nested
    @DisplayName("[DELETE] /api/v1/games/cancel/{gameId} - cancel a crossword game")
    inner class CancelCrosswordGame {
        lateinit var authenticatedUser: MockedAuthenticatedUser
        lateinit var ongoingCrosswordSavedInDb: OngoingCrosswordGameDTO

        @BeforeEach
        fun beforeEach() {
            authenticatedUser = mockAuthenticatedUser()

            ongoingCrosswordSavedInDb = crosswordGameMocker.mockFromJsonSource(authenticatedUser.userInfo.id).first
        }

        @AfterEach
        fun afterEach() {
            userRepository.deleteById(authenticatedUser.userInfo.id).block()
        }

        private fun cancelGame(
            authenticatedUser: MockedAuthenticatedUser? = this.authenticatedUser,
            expectedStatus: HttpStatus = HttpStatus.NO_CONTENT
        ) {
            val response = crosswordGameAPIClient.cancelGame(
                gameId = ongoingCrosswordSavedInDb.id,
                user = authenticatedUser
            )

            response.status shouldBe expectedStatus
        }

        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            @Test
            fun `200 - Crossword game can be canceled`() {
                cancelGame()

                ongoingGameRepository.findById(ongoingCrosswordSavedInDb.id).block() shouldBe null
                finishedGameRepository.findAllByUserId(authenticatedUser.userInfo.id).collectList().block()!!.first().let {
                    it shouldNotBe null

                    it.result shouldBe GameResult.CANCELLED
                    it.language shouldBe ongoingCrosswordSavedInDb.language
                    it.difficulty shouldBe ongoingCrosswordSavedInDb.difficulty
                }
            }

            @Test
            fun `200 - User activity log should be assigned after canceling the game`() {
                cancelGame()

                val logs = userActivityLogRepository.findAllByUserId(authenticatedUser.userInfo.id).collectList().block()!!
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
                    expectedStatus = HttpStatus.NOT_FOUND
                )
            }

            @Test
            fun `401 - Unauthorized user cannot cancel a crossword game`() {
                cancelGame(
                    authenticatedUser = null,
                    expectedStatus = HttpStatus.UNAUTHORIZED
                )

                ongoingGameRepository.findById(ongoingCrosswordSavedInDb.id).block() shouldNotBe null
            }

            @Test
            fun `404 - User cannot cancel somebody else game`() {
                cancelGame(
                    authenticatedUser = mockAuthenticatedUser(),
                    expectedStatus = HttpStatus.NOT_FOUND
                )

                ongoingGameRepository.findById(ongoingCrosswordSavedInDb.id).block() shouldNotBe null
            }
        }
    }
}