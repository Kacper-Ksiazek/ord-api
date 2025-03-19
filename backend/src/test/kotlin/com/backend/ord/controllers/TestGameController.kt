package com.backend.ord.controllers

import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.controllers.utils_for_testing.bases.GameControllerTestBase
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.repositories.UserActivityLogRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- CrosswordGameController")
class TestGameController @Autowired constructor(
    objectMapper: ObjectMapper,
    private val userActivityLogRepository: UserActivityLogRepository,
) : GameControllerTestBase(objectMapper) {
    @Nested
    @DisplayName("[DELETE] /api/v1/games/cancel/{gameId} - cancel a crossword game")
    inner class CancelCrosswordGame {
        lateinit var authenticatedUser: MockedAuthenticatedUser
        lateinit var crosswordSavedInDb: CrosswordGameDTO

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

        private fun cancelGame(
            authenticatedUser: MockedAuthenticatedUser? = this.authenticatedUser,
            expectedStatus: Int = HttpStatus.NO_CONTENT.value()
        ) {
            mockMvc.perform(
                gameRequestFactory.cancelGameRequest(
                    authenticatedUser = authenticatedUser,
                    gameId = crosswordSavedInDb.id
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

                gameRepository.findByIdOrNull(crosswordSavedInDb.id)?.status shouldBe GameStatus.CANCELED
            }

            @Test
            fun `200 - User activity log should be assigned after canceling the game`() {
                cancelGame()

                val logs = userActivityLogRepository.findAllForUser(authenticatedUser.userInfo.id)
                logs shouldHaveSize 1

                logs.first().let {
                    it.type shouldBe UserActivityType.GAME_QUIT
                    it.language shouldBe crosswordSavedInDb.language
                    it.points shouldBe UserActivityType.GAME_QUIT.points
                    it.gameDifficulty shouldBe crosswordSavedInDb.difficulty
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Negative {
            @Test
            fun `400 - Game cannot be cancelled twice`() {
                cancelGame()

                cancelGame(
                    expectedStatus = HttpStatus.BAD_REQUEST.value()
                )
            }

            @Test
            fun `403 - Unauthorized user cannot cancel a crossword game`() {
                cancelGame(
                    authenticatedUser = null,
                    expectedStatus = HttpStatus.FORBIDDEN.value()
                )

                gameRepository.findByIdOrNull(crosswordSavedInDb.id)?.status shouldBe crosswordSavedInDb.status
            }

            @Test
            fun `404 - User cannot cancel somebody else game`() {
                cancelGame(
                    authenticatedUser = mockAuthenticatedUser(),
                    expectedStatus = HttpStatus.NOT_FOUND.value()
                )

                gameRepository.findByIdOrNull(crosswordSavedInDb.id)?.status shouldBe crosswordSavedInDb.status
            }

        }


    }
}