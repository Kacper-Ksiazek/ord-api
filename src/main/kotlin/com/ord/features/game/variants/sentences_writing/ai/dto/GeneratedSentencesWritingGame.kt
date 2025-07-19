package com.ord.features.game.variants.sentences_writing.ai.dto

import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction
import com.ord.features.game.variants.shared.ai.GeneratedGame

typealias GeneratedSentencesWritingGame = GeneratedGame<
        SentencesWritingInstruction,
        SentencesWritingProperAnswers
        >
