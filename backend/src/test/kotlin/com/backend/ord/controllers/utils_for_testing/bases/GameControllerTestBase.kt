package com.backend.ord.controllers.utils_for_testing.bases

import com.backend.ord.controllers.request_factories.GameRequestFactory
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.repositories.GameRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.seeders.mocks.games.MockCrosswordGames
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

@AutoConfigureMockMvc
abstract class GameControllerTestBase(
    objectMapper: ObjectMapper
) : ControllerTestBase(objectMapper = objectMapper) {
    @Autowired
    lateinit var gameMapper: GameMapper

    @Autowired
    lateinit var wordRepository: WordRepository

    @Autowired
    lateinit var gameRepository: GameRepository

    @Autowired
    lateinit var wordMockFactory: WordMockFactory

    @Autowired
    lateinit var mockCrosswordGames: MockCrosswordGames

    val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)

    internal fun prepareCrosswordGame(difficulty: GameDifficulty = GameDifficulty.HARD): Pair<MockedAuthenticatedUser, CrosswordGameDTO> {
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