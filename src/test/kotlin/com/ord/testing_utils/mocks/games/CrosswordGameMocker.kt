package com.ord.testing_utils.mocks.games

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.crossword.dto.CrosswordInstruction
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api.clients.games.bases.GameAPIClient
import com.ord.testing_utils.dto.resources.mocks.games.CrosswordInJson
import java.util.*

class CrosswordGameMocker(
    val apiClient: GameAPIClient<StartedCrosswordGameResponse, *, *>,
    val ongoingGameMapper: OngoingGameMapper,
    val ongoingGameRepository: OngoingGameRepository,
    val wordMockFactory: WordFactory,
    val wordRepository: WordRepository,
) : GameMockerBase<
        CrosswordInJson,
        OngoingCrosswordGameDTO,
        CrosswordInstruction,
        StartedCrosswordGameResponse
        >(
    ongoingGameMapper = ongoingGameMapper,
    ongoingGameRepository = ongoingGameRepository,
    wordMockFactory = wordMockFactory,
    wordRepository = wordRepository,
    apiClient = apiClient,
) {
    override val pathToJsonFile: String = "mocks/games/crosswords.json"

    override val jsonFileContentTypeRef: TypeReference<List<CrosswordInJson>> =
        object : TypeReference<List<CrosswordInJson>>() {}

    override fun createOngoingGameEntity(
        jsonData: CrosswordInJson,
        userId: UUID,
    ): OngoingGameEntity {
        return OngoingGameEntity(
            properAnswers = ongoingGameMapper.serializeProperAnswers(jsonData.properAnswers),
            type = GameType.CROSSWORD,
            language = jsonData.language,
            difficulty = jsonData.difficulty,
            userId = userId
        )
    }

    override fun getListOfUsedWords(ongoingGameDTO: OngoingCrosswordGameDTO): Set<String> {
        return ongoingGameDTO.properAnswers.questions.values.toSet()
    }
}