package com.backend.ord.controllers.utils_for_testing

import com.backend.ord.api.requests.games.data.CrosswordUserAnswersQuestionData
import com.backend.ord.enums.application.game.ScoringResult
import jakarta.validation.constraints.Min
import java.util.*

data class AlteredProperAnswer(
    val questionId: UUID,
    val originalAnswer: String,
    val alteredAnswer: String,

    val desiredResult: ScoringResult
)

private fun CrosswordUserAnswersQuestionData.makeArtificialMistake(desiredResult: ScoringResult): AlteredProperAnswer {
    val alteredAnswer = when (desiredResult) {
        ScoringResult.INCORRECT -> "x".repeat(word.length)
        ScoringResult.HALF_CORRECT -> "x${word.slice(1 until word.length)}"
        else -> {
            throw IllegalArgumentException("desiredResult must to be either INCORRECT or HALF_CORRECT")
        }
    }

    return AlteredProperAnswer(
        questionId = id,
        originalAnswer = word,
        alteredAnswer = alteredAnswer,
        desiredResult = desiredResult
    )
}

fun Set<CrosswordUserAnswersQuestionData>.mockAnswersWithMistakes(
    mistakes: Map<ScoringResult, @Min(0) Int>
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
            val question: CrosswordUserAnswersQuestionData = remainingQuestions.random()
            val alteredAnswer = question.properAnswer.alterAnswer(scoringResult)

            result.add(
                AlteredProperAnswer(
                    questionId = question.id,
                    originalAnswer = question.word,
                    alteredAnswer = alteredAnswer
                )
            )

            remainingQuestions.remove(question)
        }
    }


    return result.toSet()
}