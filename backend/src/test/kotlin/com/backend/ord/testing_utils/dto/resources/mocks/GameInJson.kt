package com.backend.ord.testing_utils.dto.resources.mocks

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.enums.GameType

interface GameInJson<TGameInstruction, TGameProperAnswers> {
    val type: GameType
    val language: LanguageName
    val difficulty: GameDifficulty
    val instruction: TGameInstruction
    val properAnswers: TGameProperAnswers
}