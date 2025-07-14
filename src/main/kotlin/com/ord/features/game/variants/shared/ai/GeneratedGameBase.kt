package com.ord.features.game.variants.shared.ai

data class GeneratedGameBase<GameInstruction, ProperAnswers>(
    val instruction: GameInstruction,
    val properAnswers: ProperAnswers
)