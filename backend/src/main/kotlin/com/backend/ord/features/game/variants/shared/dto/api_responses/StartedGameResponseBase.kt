package com.backend.ord.features.game.variants.shared.dto.api_responses

import java.util.*

interface StartedGameResponseBase<GameInstruction, ProperAnswers> {
    val gameId: UUID
    val instruction: GameInstruction

    // TODO: Remove this ( its for development purposes only )
    val properAnswers: ProperAnswers
}