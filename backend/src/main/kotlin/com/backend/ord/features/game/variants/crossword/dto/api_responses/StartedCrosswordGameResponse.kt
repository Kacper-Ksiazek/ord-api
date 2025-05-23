package com.backend.ord.features.game.variants.crossword.dto.api_responses

import com.backend.ord.features.game.model.json.CrosswordProperAnswers
import com.backend.ord.features.game.variants.crossword.dto.CrosswordInstruction
import com.backend.ord.features.game.variants.shared.dto.api_responses.StartedGameResponseBase
import java.util.*

data class StartedCrosswordGameResponse(
    override val gameId: UUID,
    override val instruction: CrosswordInstruction,
    override val properAnswers: CrosswordProperAnswers
) : StartedGameResponseBase<CrosswordInstruction, CrosswordProperAnswers>
