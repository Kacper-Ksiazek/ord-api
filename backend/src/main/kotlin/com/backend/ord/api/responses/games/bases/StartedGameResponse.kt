package com.backend.ord.api.responses.games.bases

import java.util.*

interface StartedGameResponse<GameInstruction> {
    val gameId: UUID
    val instruction: GameInstruction
}
