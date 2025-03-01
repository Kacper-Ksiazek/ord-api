package com.backend.ord.api.requests.games.data

import com.backend.ord.api.requests.games.CrosswordToFinishRequest
import com.backend.ord.api.requests.games.CrosswordUserAnswers
import jakarta.validation.Valid
import java.util.*


data class CrosswordUserAnswersData(
    override val answer: String,

    @field:Valid
    override val questionsAnswers: Set<WordUserAnswer>
) : CrosswordUserAnswers


data class CrosswordToFinishRequestData(
    override val gameId: UUID,

    override val duration: String,

    @field:Valid
    override val userAnswers: CrosswordUserAnswersData
) : CrosswordToFinishRequest
