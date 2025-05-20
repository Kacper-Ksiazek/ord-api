package com.backend.ord.testing_utils.dto

import com.backend.ord.features.ongoing_game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.backend.ord.features.ongoing_game.variants.shared.enums.AnswerScore
import java.util.*

data class AlteredWordProperAnswer(
    val questionId: UUID,
    val originalAnswer: String,
    val alteredAnswer: String,

    val desiredScore: AnswerScore
)

fun Set<AlteredWordProperAnswer>.toRequestBody(perfectAnswers: Set<WordUserAnswer>): Set<WordUserAnswer> {
    return perfectAnswers.toMutableSet().map { answer ->
        val correspondingAlteredAnswer = find { it.questionId == answer.id }

        return@map if (correspondingAlteredAnswer == null) answer
        else answer.copy(answer = correspondingAlteredAnswer.alteredAnswer)
    }.toSet()
}
