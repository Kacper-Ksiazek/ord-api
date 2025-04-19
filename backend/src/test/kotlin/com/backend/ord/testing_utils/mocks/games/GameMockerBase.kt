package com.backend.ord.testing_utils.mocks.games

import com.backend.ord.domain.persistence.dto.OngoingGameDTO
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.backend.ord.testing_utils.dto.resources.mocks.GameInJson

interface GameMockerBase<
        TJSONDataModelType : GameInJson<TGameInstruction, *>,  // eg. CrosswordInJson
        TOngoingGameDTO : OngoingGameDTO<*>,                   // eg. OngoingCrosswordGameDTO
        TGameInstruction,                                      // eg. CrosswordInstruction
        TAPIResponseDTO                                        // eg. StartedCrosswordGameResponse
        > : ResourceJSONFileReader<List<TJSONDataModelType>, TJSONDataModelType> {

    /**
     * **FAST way** of mocking an ongoing game.
     * Loads predefined data from JSON file, saves it to the database and returns it.
     */
    fun mockFromJsonSource(
        userDTO: UserDTO,
        difficulty: GameDifficulty = GameDifficulty.HARD
    ): Pair<TOngoingGameDTO, TGameInstruction>

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