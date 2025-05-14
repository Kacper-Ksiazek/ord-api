package com.backend.ord.services.ai.dto.generated_games

data class GeneratedGameBase<GameInstruction, ProperAnswers>(
    val instruction: GameInstruction,
    val properAnswers: ProperAnswers
)
