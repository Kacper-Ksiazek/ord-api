package com.backend.ord.api.responses.games.bases

import com.backend.ord.domain.application.games.SentencesWritingInstruction
import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.application.games.words_typing.WordsTypingInstruction
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.jsons.game_proper_answers.SentencesWritingProperAnswers
import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers
import java.util.*

data class StartedGameResponse<GameInstruction, ProperAnswers>(
    val gameId: UUID,
    val instruction: GameInstruction,

    // TODO: Remove this ( its for development purposes only )
    val properAnswers: ProperAnswers
)

typealias StartedCrosswordGameResponse = StartedGameResponse<CrosswordInstruction, CrosswordProperAnswers>
typealias StartedWordsTypingGameResponse = StartedGameResponse<WordsTypingInstruction, WordsTypingProperAnswers>
typealias StartedSentencesWritingGameResponse = StartedGameResponse<SentencesWritingInstruction, SentencesWritingProperAnswers>
