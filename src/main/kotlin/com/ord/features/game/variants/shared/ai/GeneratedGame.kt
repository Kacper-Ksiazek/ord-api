package com.ord.features.game.variants.shared.ai

data class GeneratedGame<GameInstruction, ProperAnswers>(
    val instruction: GameInstruction,
    val properAnswers: ProperAnswers
)