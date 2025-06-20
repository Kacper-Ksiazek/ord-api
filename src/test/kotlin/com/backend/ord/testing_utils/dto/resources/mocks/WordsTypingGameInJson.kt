package com.backend.ord.testing_utils.dto.resources.mocks

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.enums.GameType
import com.backend.ord.features.game.model.ongoing_game.json.WordsTypingProperAnswers
import com.backend.ord.features.game.variants.words_typing.dto.WordsTypingInstruction

data class WordsTypingGameInJson(
    override val type: GameType = GameType.WORDS_TYPING,
    override val language: LanguageName,
    override val difficulty: GameDifficulty,
    override val instruction: WordsTypingInstruction,
    override val properAnswers: WordsTypingProperAnswers
) : GameInJson<WordsTypingInstruction, WordsTypingProperAnswers>