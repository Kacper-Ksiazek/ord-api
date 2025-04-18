package com.backend.ord.controllers.helpers.mocks.bases

import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader

interface GameMockerBase<TFileContent,       // eg. List<CrosswordInJSON>
        TJSONDataModelType, // eg. CrosswordInJSON
        TOngoingGameDTO,    // eg. OngoingCrosswordGameDTO
        TGameInstruction,   // eg. CrosswordInstruction
        TAPIResponseDTO     // eg. StartedCrosswordGameResponse
        > : ResourceJSONFileReader<TFileContent, TJSONDataModelType> {

    fun mockFromJsonSource(): Pair<TOngoingGameDTO, TGameInstruction>

    fun mockThroughApiFlow(): Pair<TOngoingGameDTO, TAPIResponseDTO>
}