package com.ord.testing_utils.mocks.games

import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserMapper
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.crossword.dto.CrosswordInstruction
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.ord.testing_utils.dto.resources.mocks.games.CrosswordInJson
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
    override val wordMockFactory: WordFactory,
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
    override val apiResponseTypeRef: TypeReference<StartedCrosswordGameResponse> =
        object : TypeReference<StartedCrosswordGameResponse>() {}

    override val jsonFileContentTypeRef: TypeReference<List<CrosswordInJson>> =
        object : TypeReference<List<CrosswordInJson>>() {}

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