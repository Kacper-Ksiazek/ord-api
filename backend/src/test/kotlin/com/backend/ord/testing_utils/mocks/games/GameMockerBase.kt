package com.backend.ord.testing_utils.mocks.games

import com.backend.ord.domain.persistence.dto.OngoingGameDTO
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.repositories.OngoingGameRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.backend.ord.testing_utils.dto.resources.mocks.GameInJson

interface GameMockerBase<
        TJSONDataModelType : GameInJson<TGameInstruction, *>,  // eg. CrosswordInJson
        TOngoingGameDTO : OngoingGameDTO<*>,                   // eg. OngoingCrosswordGameDTO
        TGameInstruction,                                      // eg. CrosswordInstruction
        TAPIResponseDTO                                        // eg. StartedCrosswordGameResponse
        > : ResourceJSONFileReader<List<TJSONDataModelType>, TJSONDataModelType> {
    val userMapper: UserMapper
    val wordRepository: WordRepository
    val wordMockFactory: WordMockFactory
    val ongoingGameMapper: OngoingGameMapper
    val ongoingGameRepository: OngoingGameRepository

    /**
     * **FAST way** of mocking an ongoing game.
     * Loads predefined data from JSON file, saves it to the database and returns it.
     */
    fun mockFromJsonSource(
        userDTO: UserDTO,
        difficulty: GameDifficulty = GameDifficulty.HARD
    ): Pair<TOngoingGameDTO, TGameInstruction> {
        val (ongoingGameDTO, instruction) = loadDataFromJSONFile(userDTO)
            .filter { it.first.difficulty == difficulty }
            .random()
            .let {
                val savedOngoingGame = ongoingGameRepository.save(
                    ongoingGameMapper.toEntity(it.first)
                )

                val updatedDTO = it.first.copy(id = savedOngoingGame.id)

                @Suppress("UNCHECKED_CAST")
                Pair(updatedDTO as TOngoingGameDTO, it.second)
            }

        val currentWords = wordRepository
            .findAllForUser(userDTO.id)
            .filter { it.translatedFrom == ongoingGameDTO.language }
            .map { it.origin }

        wordRepository.saveAll(
            getListOfUsedWords(ongoingGameDTO)
                .filter { it !in currentWords }
                .map {
                    wordMockFactory.mockEntity(
                        origin = it,
                        translatedFrom = ongoingGameDTO.language,
                        user = userMapper.toEntity(userDTO),
                    )
                }
        )

        return Pair(ongoingGameDTO, instruction)
    }

    /**
     * **SLOW way** of mocking an ongoing game.
     * Makes a proper API call to the server, waits for AI to generate an actual game,
     * saves it to the database and returns it.
     */
    fun mockThroughApiFlow(
        authenticatedUser: MockedAuthenticatedUser,
        difficulty: GameDifficulty = GameDifficulty.HARD
    ): Pair<TOngoingGameDTO, TAPIResponseDTO>

    /**
     * Utility function to process the JSON row into an ongoing game DTO.
     */
    fun createOngoingGameDTO(
        jsonData: TJSONDataModelType,
        userDTO: UserDTO
    ): TOngoingGameDTO

    /**
     * Utility function to extract the list of used words from the ongoing game DTO.
     */
    fun getListOfUsedWords(ongoingGameDTO: TOngoingGameDTO): Set<String>

    // TODO: Rename JSON to Json
    /**
     * Reads the JSON file, process its content and returns a list of pairs
     * of ongoing game DTOs and their instructions.
     */
    fun loadDataFromJSONFile(
        userDTO: UserDTO
    ): List<Pair<TOngoingGameDTO, TGameInstruction>> {
        val jsonData: List<TJSONDataModelType> = readFromJSONFile()

        return jsonData.map {
            Pair(
                createOngoingGameDTO(it, userDTO),
                it.instruction
            )
        }
    }
}