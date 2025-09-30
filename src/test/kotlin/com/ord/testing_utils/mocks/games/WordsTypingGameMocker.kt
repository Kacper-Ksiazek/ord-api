package com.ord.testing_utils.mocks.games

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.OngoingWordsTypingGameDTO
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.words_typing.dto.WordsTypingInstruction
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api.clients.bases.GameAPIClient
import com.ord.testing_utils.dto.resources.mocks.games.WordsTypingGameInJson
import java.util.*

class WordsTypingGameMocker(
    val apiClient: GameAPIClient<StartedWordsTypingGameResponse, *, *>,
    val ongoingGameMapper: OngoingGameMapper,
    val ongoingGameRepository: OngoingGameRepository,
    val wordMockFactory: WordFactory,
    val wordRepository: WordRepository,
) : GameMockerBase<
        WordsTypingGameInJson,
        OngoingWordsTypingGameDTO,
        WordsTypingInstruction,
        StartedWordsTypingGameResponse
        >(
    ongoingGameMapper = ongoingGameMapper,
    ongoingGameRepository = ongoingGameRepository,
    wordMockFactory = wordMockFactory,
    wordRepository = wordRepository,
    apiClient = apiClient,
) {
    override val pathToJsonFile: String = "mocks/games/words_typing.json"

    override val jsonFileContentTypeRef: TypeReference<List<WordsTypingGameInJson>> =
        object : TypeReference<List<WordsTypingGameInJson>>() {}

    override fun createOngoingGameEntity(
        jsonData: WordsTypingGameInJson,
        userId: UUID,
    ): OngoingGameEntity {
        return OngoingGameEntity(
            properAnswers = ongoingGameMapper.serializeProperAnswers(jsonData.properAnswers),
            type = GameType.WORDS_TYPING,
            language = jsonData.language,
            difficulty = jsonData.difficulty,
            userId = userId
        )
    }

    override fun getListOfUsedWords(ongoingGameDTO: OngoingWordsTypingGameDTO): Set<String> {
        return ongoingGameDTO.properAnswers.values.toSet()
    }
}