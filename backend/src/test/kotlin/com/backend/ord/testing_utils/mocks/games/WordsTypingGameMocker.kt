package com.backend.ord.testing_utils.mocks.games

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.core.word.repository.WordRepository
import com.backend.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.backend.ord.features.game.model.ongoing_game.OngoingWordsTypingGameDTO
import com.backend.ord.features.game.model.ongoing_game.enums.GameType
import com.backend.ord.features.game.repositories.OngoingGameRepository
import com.backend.ord.features.game.variants.words_typing.dto.WordsTypingInstruction
import com.backend.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.backend.ord.testing_utils.dto.resources.mocks.WordsTypingGameInJson
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.MockMvc


class WordsTypingGameMocker(
    override val mockMvc: MockMvc,
    override val userMapper: UserMapper,
    override val objectMapper: ObjectMapper,
    override val wordRepository: WordRepository,
    override val ongoingGameMapper: OngoingGameMapper,
    override val ongoingGameRepository: OngoingGameRepository,
    override val wordMockFactory: WordMockFactory,
) : GameMockerBase<
        WordsTypingGameInJson,
        OngoingWordsTypingGameDTO,
        WordsTypingInstruction,
        StartedWordsTypingGameResponse
        > {
    // Properties:
    override val mockingGameType: GameType = GameType.WORDS_TYPING
    override val pathToJsonFile: String = "mocks/games/words_typing.json"

    // Classes & type references:
    override val apiResponseTypeRef: TypeReference<StartedWordsTypingGameResponse> =
        object : TypeReference<StartedWordsTypingGameResponse>() {}

    override val jsonFileContentTypeRef: TypeReference<List<WordsTypingGameInJson>> =
        object : TypeReference<List<WordsTypingGameInJson>>() {}

    // Dependencies:
    override val gameRequestFactory: GameRequestFactory = GameRequestFactory(objectMapper)

    override fun createOngoingGameDTO(
        jsonData: WordsTypingGameInJson,
        userDTO: UserDTO
    ): OngoingWordsTypingGameDTO {
        return OngoingWordsTypingGameDTO(
            properAnswers = jsonData.properAnswers,
            type = GameType.WORDS_TYPING,
            language = jsonData.language,
            difficulty = jsonData.difficulty,
            user = userDTO
        )
    }

    override fun getListOfUsedWords(ongoingGameDTO: OngoingWordsTypingGameDTO): Set<String> {
        return ongoingGameDTO.properAnswers.values.toSet()
    }
}
