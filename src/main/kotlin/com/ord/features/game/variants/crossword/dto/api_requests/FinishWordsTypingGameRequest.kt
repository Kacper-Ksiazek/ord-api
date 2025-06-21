package com.ord.features.game.variants.crossword.dto.api_requests

import com.ord.features.game.variants.shared.dto.api_requests.bases.FinishGameRequestDataBase
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import jakarta.validation.Valid
import java.util.*

data class FinishWordsTypingGameRequest(
    override val gameId: UUID,
    override val duration: String,

    @field:Valid override val answers: Set<WordUserAnswer>
) : FinishGameRequestDataBase<Set<WordUserAnswer>>

