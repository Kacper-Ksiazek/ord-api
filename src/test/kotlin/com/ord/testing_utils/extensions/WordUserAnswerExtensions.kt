package com.ord.testing_utils.extensions

import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import com.ord.testing_utils.dto.AlteredWordProperAnswer
import jakarta.validation.constraints.Min

private fun WordUserAnswer.makeArtificialMistake(desiredResult: WordAnswerScore): AlteredWordProperAnswer {
    val alteredAnswer = when (desiredResult) {
        WordAnswerScore.INCORRECT -> "x".repeat(answer.length)
        WordAnswerScore.HALF_CORRECT -> "x${answer.drop(1)}"
        else -> {
            throw IllegalArgumentException("desiredResult must to be either INCORRECT or HALF_CORRECT")
        }
    }

    return AlteredWordProperAnswer(
        questionId = id,
        originalAnswer = answer,
        alteredAnswer = alteredAnswer,
        desiredScore = desiredResult
    )
}

fun Set<WordUserAnswer>.mockAnswersWithMistakes(
    mistakes: Map<WordAnswerScore, @Min(0) Int>
): Set<AlteredWordProperAnswer> {
    if (mistakes.isEmpty()) {
        throw IllegalArgumentException("Mistakes set cannot be empty")
    }

    if (mistakes.values.sum() > this.size) {
        throw IllegalArgumentException("Requested more mistakes than provided words")
    }

    val result: MutableSet<AlteredWordProperAnswer> = mutableSetOf()
    val remainingQuestions = this.toMutableSet()

    for ((scoringResult, mistakesCount) in mistakes) {
        for (i in 0 until mistakesCount) {
            val question: WordUserAnswer = remainingQuestions.random()

            result.add(
                question.makeArtificialMistake(scoringResult)
            )

            remainingQuestions.remove(question)
        }
    }

    return result.toSet()
}