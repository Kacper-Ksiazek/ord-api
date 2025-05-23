package com.backend.ord.features.game.variants.words_typing.dto.api_responses

import com.backend.ord.features.game.model.json.WordsTypingProperAnswers
import com.backend.ord.features.game.variants.shared.dto.api_responses.StartedGameResponseBase
import com.backend.ord.features.game.variants.words_typing.dto.WordsTypingInstruction
import java.util.*

data class StartedWordsTypingGameResponse(
    override val gameId: UUID,
    override val instruction: WordsTypingInstruction,
    override val properAnswers: WordsTypingProperAnswers
) : StartedGameResponseBase<WordsTypingInstruction, WordsTypingProperAnswers>
