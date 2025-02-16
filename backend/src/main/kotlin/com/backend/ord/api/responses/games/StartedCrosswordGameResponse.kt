package com.backend.ord.api.responses.games

import com.backend.ord.api.responses.games.bases.StartedGameResponse
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.persistence.embedded.game_proper_answers.CrosswordProperAnswers
import java.util.*

data class StartedCrosswordGameResponse(
    override val gameId: UUID,
    override val instruction: CrosswordInstruction,

    // TODO: Remove this ( its for development purposes only )
    val properAnswers: CrosswordProperAnswers
) : StartedGameResponse<CrosswordInstruction>