package com.backend.ord.testing_utils.dto.resources.mocks

import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName

data class CrosswordInJson(
    override val type: GameType = GameType.CROSSWORD,
    override val language: LanguageName,
    override val difficulty: GameDifficulty,
    override val instruction: CrosswordInstruction,
    override val properAnswers: CrosswordProperAnswers
) : GameInJson<CrosswordInstruction, CrosswordProperAnswers>
