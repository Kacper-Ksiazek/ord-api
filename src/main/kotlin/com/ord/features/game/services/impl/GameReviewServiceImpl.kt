package com.ord.features.game.services.impl

import com.ord.config.GamesConfig
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word_progress.WordProgressEntity
import com.ord.core.word.repositories.WordProgressRepository
import com.ord.core.word.services.WordProgressService
import com.ord.core.word.services.applyAnswerScore
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.service.UserActivityLogService
import com.ord.core.word.services.WordService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class GameReviewServiceImpl(
    private val wordProgressRepository: WordProgressRepository,
    private val wordProgressService: WordProgressService,
    private val userActivityLogService: UserActivityLogService,
    private val wordService: WordService,
) : GameReviewService {
    override fun reviewUserAnswers(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty,
    ): Set<IdentifiableReviewedWordAnswer> {
        return expectedAnswers.entries.map { (questionId, expectedAnswer) ->
            val userAnswer = userAnswers.find { it.id == questionId }
            val score = WordAnswerScore.Companion.reviewUserAnswer(
                difficulty = difficulty,
                expectedAnswer = expectedAnswer,
                userAnswer = userAnswer?.answer,
            )
            IdentifiableReviewedWordAnswer(
                id = questionId,
                expectedAnswer = expectedAnswer,
                userAnswer = userAnswer?.answer,
                score = score,
            )
        }.toSet()
    }

    override fun updateDBPointsForManyWords(
        userId: UUID,
        language: LanguageName,
        ratedWords: Map<String, WordAnswerScore>,
    ): Mono<Void> {
        val userActivityLogsToSave: MutableSet<UserActivityLogEntity> = mutableSetOf()

        return wordProgressService.findAllByOrigins(
            origins = ratedWords.keys,
            language = language,
            userId = userId,
        )
            .map { item ->
                val score = ratedWords[item.sourceWord] ?: WordAnswerScore.INCORRECT
                val progress = item.progress
                val wasFirstCompletedBefore = progress.firstCompletedAt != null
                val updated = wordProgressService.applyAnswerScore(progress, score)

                if (updated.firstCompletedAt != null && !wasFirstCompletedBefore) {
                    userActivityLogsToSave.add(
                        UserActivityLogEntity(
                            userId = userId,
                            type = UserActivityType.WORD_COMPLETED,
                            language = language,
                        ),
                    )
                }
                updated
            }
            .collectList()
            .flatMap { progressToSave ->
                wordProgressRepository.saveAll(progressToSave).collectList()
            }
            .flatMap {
                wordProgressService
                    .countCompleted(language = language, userId = userId)
                    .map { completedCount ->
                        if (completedCount.today >= 10) {
                            userActivityLogsToSave.add(
                                UserActivityLogEntity(
                                    userId = userId,
                                    type = UserActivityType.WORDS_COMPLETED_IN_ONE_DAY_10,
                                    language = language,
                                )
                            )
                        }
                        if (completedCount.week >= 30) {
                            userActivityLogsToSave.add(
                                UserActivityLogEntity(
                                    userId = userId,
                                    type = UserActivityType.WORDS_COMPLETED_IN_ONE_WEEK_30,
                                    language = language,
                                )
                            )
                        }
                        userActivityLogsToSave
                    }
            }
            .flatMap { activityLogs ->
                userActivityLogService.logMany(activityLogs).then()
            }
    }
}
