package com.backend.ord.seeders.mocks.games.json_data_models

import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName

data class CrosswordInJSON(
    val type: GameType = GameType.CROSSWORD,
    val language: LanguageName,
    val difficulty: GameDifficulty,
    val instruction: CrosswordInstruction,
    val properAnswers: CrosswordProperAnswers
)
