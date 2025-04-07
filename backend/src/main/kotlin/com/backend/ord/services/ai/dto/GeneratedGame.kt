package com.backend.ord.services.ai.dto

import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.application.games.words_typing.WordsTypingInstruction
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers

data class GeneratedGame<GameInstruction, ProperAnswers>(
    val instruction: GameInstruction,
    val properAnswers: ProperAnswers
)

typealias GeneratedCrosswordGame = GeneratedGame<CrosswordInstruction, CrosswordProperAnswers>

typealias GeneratedWordsTypingGame = GeneratedGame<WordsTypingInstruction, WordsTypingProperAnswers>