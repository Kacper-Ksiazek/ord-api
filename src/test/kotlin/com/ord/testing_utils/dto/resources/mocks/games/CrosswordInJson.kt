package com.ord.testing_utils.dto.resources.mocks.games

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.CrosswordProperAnswers
import com.ord.features.game.variants.crossword.dto.CrosswordInstruction

data class CrosswordInJson(
    override val type: GameType = GameType.CROSSWORD,
    override val language: LanguageName,
    override val difficulty: GameDifficulty,
    override val instruction: CrosswordInstruction,
    override val properAnswers: CrosswordProperAnswers
) : GameInJson<CrosswordInstruction, CrosswordProperAnswers>
