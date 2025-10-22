package com.ord.testing_utils.mocks.games

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.repositories.WordRepository
import com.ord.features.game.model.ongoing_game.OngoingGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.shared.dto.api_requests.UnsafeStartGameRequestData
import com.ord.features.game.variants.shared.dto.api_responses.StartedGameResponseBase
import com.ord.seeders.factories.WordFactory
import com.ord.seeders.mocks.bases.ResourceJSONFileReader
import com.ord.seeders.mocks.bases.RootDir
import com.ord.testing_utils.api.clients.games.bases.GameAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.dto.resources.mocks.games.GameInJson
import com.ord.utils.resource_readers.loadWordsFromResourceFile
import java.util.*

abstract class GameMockerBase<
        TJSONDataModelType : GameInJson<TGameInstruction, *>,        // eg. CrosswordInJson
        TOngoingGameDTO : OngoingGameDTO<*>,                         // eg. OngoingCrosswordGameDTO
        TGameInstruction,                                            // eg. CrosswordInstruction
        TStartedGameResponseBody : StartedGameResponseBase<TGameInstruction, *>   // eg. StartedCrosswordGameResponse
        >(
    private val ongoingGameMapper: OngoingGameMapper,
    private val ongoingGameRepository: OngoingGameRepository,
    private val wordMockFactory: WordFactory,
    private val wordRepository: WordRepository,
    private val apiClient: GameAPIClient<TStartedGameResponseBody, *, *>,
) : ResourceJSONFileReader<List<TJSONDataModelType>, TJSONDataModelType> {
    override val root: RootDir = RootDir.TEST_RESOURCES

    /**
     * Utility function to process the JSON row into an ongoing game DTO.
     */
    abstract fun createOngoingGameEntity(
        jsonData: TJSONDataModelType,
        userId: UUID
    ): OngoingGameEntity

    /**
     * Utility function to extract the list of used words from the ongoing game DTO.
     */
    abstract fun getListOfUsedWords(ongoingGameDTO: TOngoingGameDTO): Set<String>

    // Static
    companion object {
        object DefaultParams {
            val language: LanguageName = LanguageName.ENGLISH
            val difficulty: GameDifficulty = GameDifficulty.HARD
        }
    }

    /**
     * **FAST way** of mocking an ongoing game.
     * Loads predefined data from JSON file, saves it to the database and returns it.
     */
    fun mockFromJsonSource(
        userId: UUID,
        difficulty: GameDifficulty = GameDifficulty.HARD
    ): Pair<TOngoingGameDTO, TGameInstruction> {
        val (ongoingGameDTO, instruction) = loadDataFromJsonFile(userId)
            .filter { it.first.difficulty == difficulty }
            .random()
            .let {
                val savedOngoingGame = ongoingGameRepository.save(it.first).block()!!

                @Suppress("UNCHECKED_CAST")
                val ongoingGameDTO = ongoingGameMapper.toDTO(savedOngoingGame) as TOngoingGameDTO

                Pair(ongoingGameDTO, it.second)
            }

        val currentWords = wordRepository
            .findAllByUserId(userId)
            .collectList()
            .block()!!
            .filter { it.translatedFrom == ongoingGameDTO.language }
            .map { it.origin }

        wordRepository
            .saveAll(
                getListOfUsedWords(ongoingGameDTO)
                    .filter { it !in currentWords }
                    .map {
                        wordMockFactory.mockEntity(
                            origin = it,
                            translatedFrom = ongoingGameDTO.language,
                            userId = userId,
                        )
                    }
            )
            .collectList()
            .block()

        return Pair(ongoingGameDTO, instruction)
    }

    /**
     * **SLOW way** of mocking an ongoing game.
     * Makes a proper API call to the server, waits for AI to generate an actual game,
     * saves it to the database and returns it.
     */
    fun mockThroughApiFlow(
        authenticatedUser: MockedAuthenticatedUser,
        difficulty: GameDifficulty = DefaultParams.difficulty,
        language: LanguageName = DefaultParams.language
    ): Pair<TOngoingGameDTO, TStartedGameResponseBody> {
        loadWordsFromResourceFile(
            userId = authenticatedUser.userInfo.id,
            wordsRepository = wordRepository,
        )

        val response = apiClient.startGame(
            user = authenticatedUser,
            body = UnsafeStartGameRequestData(
                difficulty = difficulty,
                language = language
            )
        )

        val responseBody = response.body!!

        @Suppress("UNCHECKED_CAST")
        val ongoingGameDTO = ongoingGameMapper.toDTO(
            entity = ongoingGameRepository.findById(
                responseBody.gameId
            ).block()!!
        ) as TOngoingGameDTO

        return Pair(ongoingGameDTO, responseBody)
    }

    /**
     * Reads the JSON file, process its content and returns a list of pairs
     * of ongoing game DTOs and their instructions.
     */
    fun loadDataFromJsonFile(
        userId: UUID
    ): List<Pair<OngoingGameEntity, TGameInstruction>> {
        val jsonData: List<TJSONDataModelType> = readFromJSONFile()

        return jsonData.map {
            Pair(
                createOngoingGameEntity(
                    jsonData = it,
                    userId = userId
                ),
                it.instruction
            )
        }
    }
}