package com.backend.ord.api.requests.games.data

import com.backend.ord.api.requests.games.CrosswordToFinishRequest
import com.backend.ord.api.requests.games.CrosswordUserAnswers
import com.backend.ord.api.requests.games.CrosswordUserAnswersQuestion
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.util.*

data class CrosswordUserAnswersQuestionData(
    override val id: UUID,

    @field:Size(min = 1, max = 255, message = "Word must be between 1 and 255 characters")
    override val word: String,
) : CrosswordUserAnswersQuestion


data class CrosswordUserAnswersData(
    override val answer: String,

    @field:Valid
    override val questionsAnswers: Set<CrosswordUserAnswersQuestionData>
) : CrosswordUserAnswers


data class CrosswordToFinishRequestData(
    override val gameId: UUID,

    override val duration: String,

    @field:Valid
    override val userAnswers: CrosswordUserAnswersData
) : CrosswordToFinishRequest
