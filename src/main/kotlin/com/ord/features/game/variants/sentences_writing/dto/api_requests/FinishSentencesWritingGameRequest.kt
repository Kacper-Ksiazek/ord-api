package com.ord.features.game.variants.sentences_writing.dto.api_requests

import com.ord.features.game.variants.shared.dto.api_requests.bases.FinishGameRequestDataBase
import jakarta.validation.constraints.Pattern
import java.util.*

typealias FinishSentencesWritingGameAnswers = Map<UUID, String>

data class FinishSentencesWritingGameRequest(
    override val gameId: UUID,

    @field:Pattern(regexp = """^\d{2}:\d{2}:\d{2}$""", message = "Duration must be in format hh:mm:ss")
    override val duration: String,
    /**
     * Map where:
     * - `Key` - UUID of a particular topic in the game
     * - `Value` - User's answer for the topic
     */
    override val answers: FinishSentencesWritingGameAnswers
) : FinishGameRequestDataBase<FinishSentencesWritingGameAnswers>
