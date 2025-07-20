package com.ord.features.game.services

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.shared.utils.data_classes.Percentage
import java.util.*

interface GameReviewService {
    fun reviewUserAnswersAndUpdateDBPoints(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty,
        user: UserEntity,
        language: LanguageName
    ): Set<IdentifiableReviewedWordAnswer> {
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
    ): Set<IdentifiableReviewedWordAnswer>

    fun updateDBPointsForManyWords(
        user: UserEntity,
        language: LanguageName,
        reviewedQuestions: Set<IdentifiableReviewedWordAnswer>
    )

    companion object {
        /**
         * Calculated the weighted score for a module.
         * SCORE =/= POINTS
         *
         * Score - the score that is saved in the database. Represent a number which is appealing to the end user and makes
         * comparison between different games easier.
         *
         * Points - is the metric solely for algorithmic calculations. It is used to calculate the score of the module in the
         * most straightforward way.
         *
         * @param earnedPoints The total points earned by the user in the module.
         * @param pointsToEarn The total points that could be earned in the module.
         * @param moduleWeight The weight of the module as a percentage.
         * @param gameMaxScore The maximum score for the game. This is saved in the database.
         */
        fun calculatedWeightedModuleScore(
            earnedPoints: Double,
            pointsToEarn: Double,
            moduleWeight: Percentage,
            gameMaxScore: Int,
        ): Int {
            val moduleMaxPoints = moduleWeight * gameMaxScore;
            val pointsRatio = earnedPoints / (pointsToEarn);

            return (pointsRatio * moduleMaxPoints).toInt()
        }
    }
}