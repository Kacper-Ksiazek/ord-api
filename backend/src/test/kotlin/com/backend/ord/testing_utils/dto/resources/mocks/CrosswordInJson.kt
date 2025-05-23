package com.backend.ord.testing_utils.dto.resources.mocks

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.features.game.model.enums.GameDifficulty
import com.backend.ord.features.game.model.enums.GameType
import com.backend.ord.features.game.model.json.CrosswordProperAnswers
import com.backend.ord.features.game.variants.crossword.dto.CrosswordInstruction

data class CrosswordInJson(
    override val type: GameType = GameType.CROSSWORD,
    override val language: LanguageName,
    override val difficulty: GameDifficulty,
    override val instruction: CrosswordInstruction,
    override val properAnswers: CrosswordProperAnswers
) : GameInJson<CrosswordInstruction, CrosswordProperAnswers>
