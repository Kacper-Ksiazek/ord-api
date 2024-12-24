package com.backend.ord.api.responses.games

import com.backend.ord.api.responses.games.bases.StartedGameResponse
import com.backend.ord.domain.embedded.game_instructions.CrosswordInstruction
import java.util.*

data class StartedCrosswordGameResponse(
    override val gameId: UUID,
    override val instruction: CrosswordInstruction
) : StartedGameResponse<CrosswordInstruction>