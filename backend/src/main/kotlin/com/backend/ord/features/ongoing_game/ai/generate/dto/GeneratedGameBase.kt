package com.backend.ord.features.ongoing_game.ai.generate.dto

data class GeneratedGameBase<GameInstruction, ProperAnswers>(
    val instruction: GameInstruction,
    val properAnswers: ProperAnswers
)
