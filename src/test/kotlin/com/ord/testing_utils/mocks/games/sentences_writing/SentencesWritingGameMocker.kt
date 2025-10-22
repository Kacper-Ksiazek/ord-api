package com.ord.testing_utils.mocks.games.sentences_writing

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.word.repositories.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.OngoingSentencesWritingGameDTO
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api.clients.games.bases.GameAPIClient
import com.ord.testing_utils.dto.resources.mocks.games.SentencesWritingInJson
import com.ord.testing_utils.mocks.games.GameMockerBase
import java.util.*

class SentencesWritingGameMocker(
    val apiClient: GameAPIClient<StartedSentencesWritingGameResponse, *, *>,
    val ongoingGameMapper: OngoingGameMapper,
    val ongoingGameRepository: OngoingGameRepository,
    val wordMockFactory: WordFactory,
    val wordRepository: WordRepository,
) : GameMockerBase<
        SentencesWritingInJson,
        OngoingSentencesWritingGameDTO,
        SentencesWritingInstruction,
        StartedSentencesWritingGameResponse
        >(
    ongoingGameMapper = ongoingGameMapper,
    ongoingGameRepository = ongoingGameRepository,
    wordMockFactory = wordMockFactory,
    wordRepository = wordRepository,
    apiClient = apiClient,
) {
    override val pathToJsonFile: String = "mocks/games/sentences_writing.json"

    override val jsonFileContentTypeRef: TypeReference<List<SentencesWritingInJson>> =
        object : TypeReference<List<SentencesWritingInJson>>() {}

    override fun createOngoingGameEntity(
        jsonData: SentencesWritingInJson,
        userId: UUID,
    ): OngoingGameEntity {
        return OngoingGameEntity(
            properAnswers = ongoingGameMapper.serializeProperAnswers(jsonData.properAnswers),
            type = GameType.SENTENCES_WRITING,
            language = jsonData.language,
            difficulty = jsonData.difficulty,
            userId = userId
        )
    }

    override fun getListOfUsedWords(ongoingGameDTO: OngoingSentencesWritingGameDTO): Set<String> {
        return ongoingGameDTO.properAnswers.map { it.word }.toSet()
    }
}
