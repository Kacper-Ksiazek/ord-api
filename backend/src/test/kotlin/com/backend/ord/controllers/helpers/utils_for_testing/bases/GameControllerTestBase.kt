package com.backend.ord.controllers.helpers.utils_for_testing.bases

import com.backend.ord.controllers.helpers.request_factories.GameRequestFactory
import com.backend.ord.controllers.helpers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.repositories.FinishedGameRepository
import com.backend.ord.repositories.OngoingGameRepository
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
    lateinit var ongoingGameMapper: OngoingGameMapper

    @Autowired
    lateinit var wordRepository: WordRepository

    @Autowired
    lateinit var ongoingGameRepository: OngoingGameRepository

    @Autowired
    lateinit var finishedGameRepository: FinishedGameRepository

    @Autowired
    lateinit var wordMockFactory: WordMockFactory

    @Autowired
    lateinit var mockCrosswordGames: MockCrosswordGames

    val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)

    internal fun prepareCrosswordGame(difficulty: GameDifficulty = GameDifficulty.HARD): Triple<
            MockedAuthenticatedUser,
            OngoingCrosswordGameDTO,
            CrosswordInstruction
            > {
        val authenticatedUser = mockAuthenticatedUser()
        val user = userMapper.toEntity(authenticatedUser.userInfo)

        val (crosswordSavedInDb, crosswordInstruction) = mockCrosswordGames
            .seedFromJSONFile(user = user)
            .filter { it.first.difficulty == difficulty }
            .random()
            .let {
                val savedOngoingGame = ongoingGameRepository.save(
                    ongoingGameMapper.toEntity(it.first)
                )
                // TODO: Come up with something smarter for this, eg. involving @PrePersist annotation of hybernate
                val updatedDTO = it.first.copy(id = savedOngoingGame.id)

                Pair(updatedDTO, it.second)
            }

        wordRepository.saveAll(
            crosswordSavedInDb.properAnswers.questions.values.map {
                wordMockFactory.mockEntity(
                    origin = it,
                    translatedFrom = crosswordSavedInDb.language,
                    user = user
                )
            }
        )

        return Triple(authenticatedUser, crosswordSavedInDb, crosswordInstruction)
    }

}