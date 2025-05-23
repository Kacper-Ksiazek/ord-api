package com.backend.ord.features.game.services

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.backend.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableProperAnswer
import com.backend.ord.utils.data_classes.Percentage
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