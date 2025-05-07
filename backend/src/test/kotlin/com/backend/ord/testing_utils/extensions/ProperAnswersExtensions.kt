package com.backend.ord.testing_utils.extensions

import com.backend.ord.api.requests.games.utils.WordUserAnswer
import com.backend.ord.api.responses.games.utils.IdentifiableProperAnswer
import com.backend.ord.testing_utils.dto.AlteredWordProperAnswer
import io.kotest.matchers.shouldBe
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

fun Set<IdentifiableProperAnswer>.assertPointsForMistakesWereAssignedProperly(
    alteredAnswers: Set<AlteredWordProperAnswer>
) {
    this.forEach { properAnswer ->
        val alteredAnswer = alteredAnswers.find { it.questionId == properAnswer.id }
        if (alteredAnswer == null) return@forEach

        properAnswer.expectedAnswer shouldBe alteredAnswer.originalAnswer
        properAnswer.score shouldBe alteredAnswer.desiredScore
    }
}