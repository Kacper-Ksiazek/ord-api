package com.backend.ord.api.responses.games

import com.backend.ord.api.responses.games.bases.StartedGameResponse
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordGameProperAnswers
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import java.util.*

data class StartedCrosswordGameResponse(
    override val gameId: UUID,
    override val instruction: CrosswordInstruction,
    val board: List<List<String?>>,


    // TODO: Remove this
    val properAnswers: CrosswordGameProperAnswers
) : StartedGameResponse<CrosswordInstruction>