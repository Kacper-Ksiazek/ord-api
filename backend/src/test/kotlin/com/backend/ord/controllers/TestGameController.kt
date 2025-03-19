package com.backend.ord.controllers

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.controllers.utils_for_testing.bases.GameControllerTestBase
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- CrosswordGameController")
class TestGameController(
    mockMvc: MockMvc?,
    objectMapper: ObjectMapper,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
) : GameControllerTestBase(
    mockMvc = mockMvc!!,
    objectMapper = objectMapper,

    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
) {
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

    private fun prepareCrosswordGame(difficulty: GameDifficulty = GameDifficulty.HARD): Pair<MockedAuthenticatedUser, CrosswordGameDTO> {
        val authenticatedUser = mockAuthenticatedUser()
        val crosswordSavedInDb = gameMapper.toCrosswordDTO(
            mockCrosswordGames.seedFromJSONFile(
                user = userMapper.toEntity(authenticatedUser.userInfo)
            ).filter { it.difficulty == difficulty }.random()
        )

        val userEntity = userMapper.toEntity(authenticatedUser.userInfo)
        wordRepository.saveAll(
            crosswordSavedInDb.properAnswers.questions.values.map {
                wordMockFactory.mockEntity(
                    origin = it,
                    translatedFrom = crosswordSavedInDb.language,
                    user = userEntity
                )
            }
        )

        return Pair(authenticatedUser, crosswordSavedInDb)
    }
}