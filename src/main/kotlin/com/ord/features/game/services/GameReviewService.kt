package com.ord.features.game.services

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import com.ord.shared.utils.data_classes.Percentage
import reactor.core.publisher.Mono
import java.util.*
import kotlin.collections.Set

interface GameReviewService {
    fun reviewUserAnswersAndUpdateDBPoints(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty,
        userId: UUID,
        language: LanguageName
    ): Mono<Set<IdentifiableReviewedWordAnswer>> {
        val reviewedQuestions = reviewUserAnswers(
            expectedAnswers = expectedAnswers,
            userAnswers = userAnswers,
            difficulty = difficulty
        )

        return updateDBPointsForManyWords(
            userId = userId,
            language = language,
            ratedWords = reviewedQuestions.associate {
                it.expectedAnswer to it.score
            }
        ).thenReturn(reviewedQuestions)
    }

    fun reviewUserAnswers(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty
    ): Set<IdentifiableReviewedWordAnswer>

    fun updateDBPointsForManyWords(
        userId: UUID,
        language: LanguageName,
        /**
         * Map of word identifiers (as Strings) to their corresponding review scores.
         * Each entry represents a word answered by the user and the score it received after review.
         */
        ratedWords: Map<String, WordAnswerScore>
    ): Mono<Void>

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