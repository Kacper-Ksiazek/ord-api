package com.backend.ord.services

import com.backend.ord.api.requests.games.data.WordUserAnswer
import com.backend.ord.enums.application.game.AnswerScore
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.utils.data_classes.Percentage
import java.util.*

interface GameReviewService {
    fun reviewUserAnswersAndUpdateDBPoints(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty,
        userId: UUID,
        language: LanguageName
    ): Set<ReviewedQuestion> {
        val reviewedQuestions = reviewUserAnswers(
            expectedAnswers = expectedAnswers,
            userAnswers = userAnswers,
            difficulty = difficulty
        )

        updateDBPointsForManyWords(
            userId = userId,
            language = language,
            reviewedQuestions = reviewedQuestions
        )

        return reviewedQuestions
    }

    fun reviewUserAnswers(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty
    ): Set<ReviewedQuestion>

    fun updateDBPointsForManyWords(
        userId: UUID,
        language: LanguageName,
        reviewedQuestions: Set<ReviewedQuestion>
    )

    companion object {
        data class ReviewedQuestion(
            val questionId: UUID,
            val properAnswer: String,
            val userAnswerScore: AnswerScore
        )

        /**
         * Compute the part of the final score for one aspect of the game
         */
        fun computeFinalScoreComponent(
            receivedPoints: Double,
            maxPoints: Double,
            moduleRatio: Percentage = Percentage(100),
            totalPointsForAllModules: Int = 100
        ): Int {
            val points: Double = (receivedPoints / maxPoints) * totalPointsForAllModules

            return (moduleRatio * points).toInt()
        }
    }
}