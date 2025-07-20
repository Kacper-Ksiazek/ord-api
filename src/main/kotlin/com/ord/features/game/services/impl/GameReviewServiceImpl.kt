package com.ord.features.game.services.impl

import com.ord.config.GamesConfig
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.model.WordEntity
import com.ord.core.word.repository.WordRepository
import com.ord.core.word.service.WordService
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.features.game.variants.shared.enums.AnswerScore
import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.service.UserActivityLogService
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class GameReviewServiceImpl(
    val wordRepository: WordRepository,
    val userActivityLogService: UserActivityLogService,
    val wordService: WordService
) : GameReviewService {
    override fun reviewUserAnswers(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty
    ): Set<IdentifiableReviewedWordAnswer> {
        return expectedAnswers.entries.map { (questionId, expectedAnswer) ->
            val userAnswer: WordUserAnswer? = userAnswers.find {
                it.id == questionId
            }

            val score = AnswerScore.Companion.reviewUserAnswer(
                difficulty = difficulty,
                expectedAnswer = expectedAnswer,
                userAnswer = userAnswer?.answer
            )

            return@map IdentifiableReviewedWordAnswer(
                id = questionId,
                expectedAnswer = expectedAnswer,
                userAnswer = userAnswer?.answer,
                score = score
            )
        }.toSet()
    }

    override fun updateDBPointsForManyWords(
        user: UserEntity,
        language: LanguageName,
        reviewedQuestions: Set<IdentifiableReviewedWordAnswer>
    ) {
        val wordsToSave: MutableSet<WordEntity> = mutableSetOf()
        val userActivityLogsToSaveEntity: MutableSet<UserActivityLogEntity> = mutableSetOf()

        wordRepository.findAllWordByTheirOrigins(
            origins = reviewedQuestions.map { it.expectedAnswer }.toSet(),
            language = language,
            userId = user.id
        ).forEach { word ->
            val points: AnswerScore = reviewedQuestions.find { it.expectedAnswer == word.origin }?.score
                ?: return@forEach

            val isWordCompletedBefore: Boolean = word.isCompleted

            word.points += points.dbPoints
            word.isCompleted = word.points >= GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD

            if (word.isCompleted && !isWordCompletedBefore) {
                word.completedAt = Instant.now()

                userActivityLogsToSaveEntity.add(
                    UserActivityLogEntity(
                        user = user,
                        type = UserActivityType.WORD_COMPLETED,
                        language = language
                    )
                )
            }

            wordsToSave.add(word)
        }

        wordRepository.saveAll(wordsToSave)

        wordService.countCompleted(language = language, userId = user.id).let {
            if (it.today >= 10) {
                userActivityLogsToSaveEntity.add(
                    UserActivityLogEntity(
                        user = user,
                        type = UserActivityType.WORDS_COMPLETED_IN_ONE_DAY_10,
                        language = language
                    )
                )
            }

            if (it.week >= 30) {
                userActivityLogsToSaveEntity.add(
                    UserActivityLogEntity(
                        user = user,
                        type = UserActivityType.WORDS_COMPLETED_IN_ONE_WEEK_30,
                        language = language
                    )
                )
            }
        }

        userActivityLogService.logMany(userActivityLogsToSaveEntity)
    }

}