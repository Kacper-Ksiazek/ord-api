package com.backend.ord.testing_utils.dto.resources.mocks

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName

interface GameInJson<TGameInstruction, TGameProperAnswers> {
    val type: GameType
    val language: LanguageName
    val difficulty: GameDifficulty
    val instruction: TGameInstruction
    val properAnswers: TGameProperAnswers
}