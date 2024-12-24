package com.backend.ord.api.requests.games.data

import com.backend.ord.api.requests.games.CrosswordToFinishRequest
import com.backend.ord.api.requests.games.CrosswordUserAnswers
import com.backend.ord.api.requests.games.CrosswordUserAnswersQuestion
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class CrosswordUserAnswersQuestionData(
    @field:NotBlank(message = "Word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Word must be between 1 and 255 characters")
    override val word: String,

    @field:NotBlank(message = "End coordinates cannot be blank")
    override val endCoordinates: Pair<Int, Int>,

    @field:NotBlank(message = "Start coordinates cannot be blank")
    override val startCoordinates: Pair<Int, Int>
) : CrosswordUserAnswersQuestion


data class CrosswordUserAnswersData(
    @field:NotBlank(message = "Answer cannot be blank")
    override val answer: String,

    @field:Valid
    @field:NotBlank(message = "Questions answers cannot be blank")
    override val questionsAnswers: Set<CrosswordUserAnswersQuestionData>
) : CrosswordUserAnswers


data class CrosswordToFinishRequestData(
    @field:NotBlank(message = "Game ID cannot be blank")
    override val gameId: UUID,

    @field:NotBlank(message = "User answers cannot be blank")
    @field:Valid
    override val userAnswers: CrosswordUserAnswersData
) : CrosswordToFinishRequest
