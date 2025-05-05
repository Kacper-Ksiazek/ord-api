package com.backend.ord.testing_utils.extensions

import com.backend.ord.api.requests.games.utils.WordUserAnswer
import java.util.*

fun Map<UUID, String>.getPerfectAnswersForQuestions(
    /**
     * If null, then all answers are going to be valid
     */
    numberOfProperAnswers: Int? = null
): Set<WordUserAnswer> {
    val limit = numberOfProperAnswers ?: this.size

    return this.entries.mapIndexed { index, (questionId, answer) ->
        WordUserAnswer(
            id = questionId,
            word = if (index < limit) answer else "__invalid__"
        )
    }.toSet()
}