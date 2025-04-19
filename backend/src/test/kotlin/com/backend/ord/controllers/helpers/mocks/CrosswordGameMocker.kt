package com.backend.ord.controllers.helpers.mocks

import com.backend.ord.api.responses.games.bases.StartedCrosswordGameResponse
import com.backend.ord.controllers.games.TestCrosswordGameController.CrosswordDefaultValues
import com.backend.ord.controllers.helpers.mocks.bases.GameMockerBase
import com.backend.ord.controllers.helpers.request_factories.GameRequestFactory
import com.backend.ord.controllers.helpers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.controllers.helpers.utils_for_testing.bases.ControllerTestBase
import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.repositories.OngoingGameRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.seeders.mocks.bases.RootDir
import com.backend.ord.seeders.mocks.games.json_data_models.CrosswordInJSON
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc

@Component
class CrosswordGameMocker @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val userMapper: UserMapper,
    private val wordRepository: WordRepository,
    private val ongoingGameMapper: OngoingGameMapper,
    private val ongoingGameRepository: OngoingGameRepository,
    private val wordMockFactory: WordMockFactory,
    private val mockMvc: MockMvc,
) : GameMockerBase<
        List<CrosswordInJSON>,
        CrosswordInJSON,
        OngoingCrosswordGameDTO,
        CrosswordInstruction,
        StartedCrosswordGameResponse
        > {
    val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)

    override val root = RootDir.MAIN_APP
    override val pathToJSONFile: String = "/games/crosswords.json"

    override fun typeReference(): TypeReference<List<CrosswordInJSON>> {
        return object : TypeReference<List<CrosswordInJSON>>() {}
    }

    override fun mockFromJsonSource(
        userDTO: UserDTO,
        difficulty: GameDifficulty
    ): Pair<OngoingCrosswordGameDTO, CrosswordInstruction> {
        val (crosswordSavedInDb, crosswordInstruction) = loadDataFromJSON(userDTO)
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
                    user = userMapper.toEntity(userDTO),
                )
            }
        )

        return Pair(crosswordSavedInDb, crosswordInstruction)
    }

    override fun mockThroughApiFlow(
        authenticatedUser: MockedAuthenticatedUser,
        difficulty: GameDifficulty
    ): Pair<OngoingCrosswordGameDTO, StartedCrosswordGameResponse> {
        loadWordsFromResourceFile(
            user = userMapper.toEntity(authenticatedUser.userInfo),
            wordsRepository = wordRepository
        )

        val request = gameRequestFactory.startGameRequest(
            gameType = GameType.CROSSWORD,
            language = CrosswordDefaultValues.language,
            difficulty = CrosswordDefaultValues.difficulty,
            authenticatedUser = authenticatedUser
        )
        val response = mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.OK.value()
            it.response
        }

        val crosswordSentToUser =
            ControllerTestBase.Companion.getResponseBody<StartedCrosswordGameResponse>(objectMapper, response)

        val crosswordSavedInDb = ongoingGameMapper.toCrosswordDTO(
            ongoingGameRepository
                .findAllForUser(authenticatedUser.userInfo.id)
                .first()
        )

        return Pair(
            crosswordSavedInDb,
            crosswordSentToUser
        )
    }

    private fun loadDataFromJSON(
        userDTO: UserDTO
    ): List<Pair<OngoingCrosswordGameDTO, CrosswordInstruction>> {
        val jsonData: List<CrosswordInJSON> = readFromJSONFile()

        return jsonData.map {
            val ongoingGameDTO = OngoingCrosswordGameDTO(
                properAnswers = it.properAnswers,
                type = GameType.CROSSWORD,
                language = it.language,
                difficulty = it.difficulty,
                user = userDTO
            )

            return@map Pair(ongoingGameDTO, it.instruction)
        }
    }
}