package com.ord.testing_utils.mocks.games.sentences_writing

/*

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserMapper
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.OngoingSentencesWritingGameDTO
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.ord.testing_utils.dto.resources.mocks.games.SentencesWritingInJson
import com.ord.testing_utils.mocks.games.GameMockerBase
import org.springframework.test.web.servlet.MockMvc

class SentencesWritingGameMocker(
    override val mockMvc: MockMvc,
    override val userMapper: UserMapper,
    override val objectMapper: ObjectMapper,
    override val wordRepository: WordRepository,
    override val ongoingGameMapper: OngoingGameMapper,
    override val ongoingGameRepository: OngoingGameRepository,
    override val wordMockFactory: WordFactory,
) : GameMockerBase<
        SentencesWritingInJson,
        OngoingSentencesWritingGameDTO,
        SentencesWritingInstruction,
        StartedSentencesWritingGameResponse
        > {
    // Properties:
    override val mockingGameType: GameType = GameType.SENTENCES_WRITING
    override val pathToJsonFile: String = "mocks/games/sentences_writing.json"

    // Classes & type references:
    override val apiResponseTypeRef: TypeReference<StartedSentencesWritingGameResponse> =
        object : TypeReference<StartedSentencesWritingGameResponse>() {}

    override val jsonFileContentTypeRef: TypeReference<List<SentencesWritingInJson>> =
        object : TypeReference<List<SentencesWritingInJson>>() {}

    // Dependencies:
    override val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)

    override fun createOngoingGameDTO(
        jsonData: SentencesWritingInJson,
        userDTO: UserDTO
    ): OngoingSentencesWritingGameDTO {
        return OngoingSentencesWritingGameDTO(
            properAnswers = jsonData.properAnswers,
            type = mockingGameType,
            language = jsonData.language,
            difficulty = jsonData.difficulty,
            user = userDTO
        )
    }

    override fun getListOfUsedWords(ongoingGameDTO: OngoingSentencesWritingGameDTO): Set<String> {
        return ongoingGameDTO.properAnswers.map { it.word }.toSet()
    }
}


 */
