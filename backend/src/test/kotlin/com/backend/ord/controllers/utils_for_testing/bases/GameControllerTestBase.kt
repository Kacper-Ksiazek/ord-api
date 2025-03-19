package com.backend.ord.controllers.utils_for_testing.bases

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.seeders.mocks.games.MockCrosswordGames
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
abstract class GameControllerTestBase(
    mockMvc: MockMvc?,
    objectMapper: ObjectMapper,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,

    protected val gameMapper: GameMapper,
    protected val userMapper: UserMapper,
    protected val wordRepository: WordRepository,
    protected val wordMockFactory: WordMockFactory,
    protected val mockCrosswordGames: MockCrosswordGames,
    // TODO: Figure out how to mock the following
) : ControllerTestBase() {

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