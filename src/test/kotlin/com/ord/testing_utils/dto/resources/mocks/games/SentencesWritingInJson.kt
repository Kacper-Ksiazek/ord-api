package com.ord.testing_utils.dto.resources.mocks.games

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction

data class SentencesWritingInJson(
    override val type: GameType,
    override val language: LanguageName,
    override val difficulty: GameDifficulty,
    override val instruction: SentencesWritingInstruction,
    override val properAnswers: SentencesWritingProperAnswers
) : GameInJson<SentencesWritingInstruction, SentencesWritingProperAnswers>