package com.ord.features.game.variants.sentences_writing.dto.api_responses

import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction
import com.ord.features.game.variants.shared.dto.api_responses.StartedGameResponseBase
import java.util.*

data class StartedSentencesWritingGameResponse(
    override val gameId: UUID,
    override val instruction: SentencesWritingInstruction,
    override val properAnswers: SentencesWritingProperAnswers
) : StartedGameResponseBase<SentencesWritingInstruction, SentencesWritingProperAnswers>
