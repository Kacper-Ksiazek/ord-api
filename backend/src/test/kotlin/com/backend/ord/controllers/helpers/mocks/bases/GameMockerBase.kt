package com.backend.ord.controllers.helpers.mocks.bases

import com.backend.ord.controllers.helpers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader

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