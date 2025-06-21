package com.ord.features.game.services

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableProperAnswer
import com.ord.shared.utils.data_classes.Percentage
import java.util.*

interface GameReviewService {
    fun reviewUserAnswersAndUpdateDBPoints(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty,
        user: UserEntity,
        language: LanguageName
    ): Set<IdentifiableProperAnswer> {
        val reviewedQuestions = reviewUserAnswers(
            expectedAnswers = expectedAnswers,
            userAnswers = userAnswers,
            difficulty = difficulty
        )

        updateDBPointsForManyWords(
            user = user,
            language = language,
            reviewedQuestions = reviewedQuestions
        )

        return reviewedQuestions
    }

    fun reviewUserAnswers(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty
    ): Set<IdentifiableProperAnswer>

    fun updateDBPointsForManyWords(
        user: UserEntity,
        language: LanguageName,
        reviewedQuestions: Set<IdentifiableProperAnswer>
    )

    companion object {
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