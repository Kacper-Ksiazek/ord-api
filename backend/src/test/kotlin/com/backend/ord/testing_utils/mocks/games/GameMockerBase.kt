package com.backend.ord.testing_utils.mocks.games

import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser

interface GameMockerBase<
        TFileContent,       // eg. List<CrosswordInJSON>
        TJSONDataModelType, // eg. CrosswordInJSON
        TOngoingGameDTO,    // eg. OngoingCrosswordGameDTO
        TGameInstruction,   // eg. CrosswordInstruction
        TAPIResponseDTO     // eg. StartedCrosswordGameResponse
        > : ResourceJSONFileReader<TFileContent, TJSONDataModelType> {

    fun mockFromJsonSource(
        userDTO: UserDTO,
        difficulty: GameDifficulty = GameDifficulty.HARD
    ): Pair<TOngoingGameDTO, TGameInstruction>

    fun mockThroughApiFlow(
        authenticatedUser: MockedAuthenticatedUser,
        difficulty: GameDifficulty = GameDifficulty.HARD
    ): Pair<TOngoingGameDTO, TAPIResponseDTO>
}