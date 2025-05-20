package com.backend.ord.testing_utils.dto.resources.mocks

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.features.ongoing_game.model.enums.GameDifficulty
import com.backend.ord.features.ongoing_game.model.enums.GameType
import com.backend.ord.features.ongoing_game.model.json.WordsTypingProperAnswers
import com.backend.ord.features.ongoing_game.variants.words_typing.dto.WordsTypingInstruction

data class WordsTypingGameInJson(
    override val type: GameType = GameType.WORDS_TYPING,
    override val language: LanguageName,
    override val difficulty: GameDifficulty,
    override val instruction: WordsTypingInstruction,
    override val properAnswers: WordsTypingProperAnswers
) : GameInJson<WordsTypingInstruction, WordsTypingProperAnswers>