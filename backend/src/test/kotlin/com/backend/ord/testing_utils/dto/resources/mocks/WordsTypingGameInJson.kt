package com.backend.ord.testing_utils.dto.resources.mocks

import com.backend.ord.domain.application.games.words_typing.WordsTypingInstruction
import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName

data class WordsTypingGameInJson(
    override val type: GameType = GameType.WORDS_TYPING,
    override val language: LanguageName,
    override val difficulty: GameDifficulty,
    override val instruction: WordsTypingInstruction,
    override val properAnswers: WordsTypingProperAnswers
) : GameInJson<WordsTypingInstruction, WordsTypingProperAnswers>