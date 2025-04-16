package com.backend.ord.controllers.helpers.utils_for_testing

import com.backend.ord.api.requests.games.utils.WordUserAnswer
import com.backend.ord.enums.application.game.AnswerScore
import jakarta.validation.constraints.Min
import java.util.*

data class AlteredProperAnswer(
    val questionId: UUID,
    val originalAnswer: String,
    val alteredAnswer: String,

    val desiredScore: AnswerScore
)

private fun WordUserAnswer.makeArtificialMistake(desiredResult: AnswerScore): AlteredProperAnswer {
    val alteredAnswer = when (desiredResult) {
        AnswerScore.INCORRECT -> "x".repeat(word.length)
        AnswerScore.HALF_CORRECT -> "x${word.drop(1)}"
        else -> {
            throw IllegalArgumentException("desiredResult must to be either INCORRECT or HALF_CORRECT")
        }
    }

    return AlteredProperAnswer(
        questionId = id,
        originalAnswer = word,
        alteredAnswer = alteredAnswer,
        desiredScore = desiredResult
    )
}

fun Set<WordUserAnswer>.mockAnswersWithMistakes(
    mistakes: Map<AnswerScore, @Min(0) Int>
): Set<AlteredProperAnswer> {
    if (mistakes.isEmpty()) {
        throw IllegalArgumentException("Mistakes set cannot be empty")
    }

    if (mistakes.values.sum() > this.size) {
        throw IllegalArgumentException("Requested more mistakes than provided words")
    }

    val result: MutableSet<AlteredProperAnswer> = mutableSetOf()
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

fun Set<AlteredProperAnswer>.toRequestBody(perfectAnswers: Set<WordUserAnswer>): Set<WordUserAnswer> {
    return perfectAnswers.toMutableSet().map { answer ->
        val correspondingAlteredAnswer = find { it.questionId == answer.id }

        return@map if (correspondingAlteredAnswer == null) answer
        else answer.copy(word = correspondingAlteredAnswer.alteredAnswer)
    }.toSet()
}