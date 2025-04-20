package com.backend.ord.testing_utils.mocks.games

import com.backend.ord.api.responses.games.bases.StartedCrosswordGameResponse
import com.backend.ord.controllers.ControllerTestBase
import com.backend.ord.controllers.games.TestCrosswordGameController
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
import com.backend.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.backend.ord.testing_utils.dto.resources.mocks.CrosswordInJson
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc

class CrosswordGameMocker(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,

    override val userMapper: UserMapper,
    override val wordRepository: WordRepository,
    override val ongoingGameMapper: OngoingGameMapper,
    override val ongoingGameRepository: OngoingGameRepository,
    override val wordMockFactory: WordMockFactory,
) : GameMockerBase<
        CrosswordInJson,
        OngoingCrosswordGameDTO,
        CrosswordInstruction,
        StartedCrosswordGameResponse
        > {
    val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)

    override val root = RootDir.TEST_RESOURCES
    override val pathToJsonFile: String = "mocks/games/crosswords.json"

    override fun typeReference(): TypeReference<List<CrosswordInJson>> {
        return object : TypeReference<List<CrosswordInJson>>() {}
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
            language = TestCrosswordGameController.CrosswordDefaultValues.language,
            difficulty = TestCrosswordGameController.CrosswordDefaultValues.difficulty,
            authenticatedUser = authenticatedUser
        )
        val response = mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.OK.value()
            it.response
        }

        val crosswordSentToUser =
            ControllerTestBase.Companion.getResponseBody<StartedCrosswordGameResponse>(objectMapper, response)

        val crosswordSavedInDb = ongoingGameMapper.toCrosswordDTO(
            ongoingGameRepository.findByIdOrNull(crosswordSentToUser.gameId)!!
        )

        return Pair(
            crosswordSavedInDb,
            crosswordSentToUser
        )
    }

    override fun createOngoingGameDTO(
        jsonData: CrosswordInJson,
        userDTO: UserDTO
    ): OngoingCrosswordGameDTO {
        return OngoingCrosswordGameDTO(
            properAnswers = jsonData.properAnswers,
            type = GameType.CROSSWORD,
            language = jsonData.language,
            difficulty = jsonData.difficulty,
            user = userDTO
        )
    }

    override fun getListOfUsedWords(ongoingGameDTO: OngoingCrosswordGameDTO): Set<String> {
        return ongoingGameDTO.properAnswers.questions.values.toSet()
    }
}