package com.backend.ord.testing_utils.mocks.games

import com.backend.ord.api.responses.games.bases.StartedCrosswordGameResponse
import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.repositories.OngoingGameRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.backend.ord.testing_utils.dto.resources.mocks.CrosswordInJson
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.MockMvc

class CrosswordGameMocker(
    override val mockMvc: MockMvc,
    override val userMapper: UserMapper,
    override val objectMapper: ObjectMapper,
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
    // Properties:
    override val mockingGameType: GameType = GameType.CROSSWORD
    override val pathToJsonFile: String = "mocks/games/crosswords.json"

    // Classes & type references:
    override val ongoingGameClass: Class<OngoingCrosswordGameDTO> = OngoingCrosswordGameDTO::class.java
    override val apiResponseClass: Class<StartedCrosswordGameResponse> = StartedCrosswordGameResponse::class.java

    override fun typeReference(): TypeReference<List<CrosswordInJson>> {
        return object : TypeReference<List<CrosswordInJson>>() {}
    }

    // Dependencies:
    override val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)

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