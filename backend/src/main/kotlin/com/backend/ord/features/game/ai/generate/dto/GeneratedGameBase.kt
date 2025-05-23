package com.backend.ord.features.game.ai.generate.dto

data class GeneratedGameBase<GameInstruction, ProperAnswers>(
    val instruction: GameInstruction,
    val properAnswers: ProperAnswers
)
