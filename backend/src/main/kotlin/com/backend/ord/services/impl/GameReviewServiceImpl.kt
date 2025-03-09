package com.backend.ord.services.impl

import com.backend.ord.api.requests.games.data.WordUserAnswer
import com.backend.ord.config.GamesConfig
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.UserActivityLog
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.enums.application.game.AnswerScore
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.repositories.UserActivityLogRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.services.GameReviewService
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class GameReviewServiceImpl(
    val wordRepository: WordRepository,
    val userActivityLogRepository: UserActivityLogRepository
) : GameReviewService {
    override fun reviewUserAnswers(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty
    ): Set<GameReviewService.Companion.ReviewedQuestion> {
        return expectedAnswers.entries.map { (questionId, expectedAnswer) ->
            val userAnswer: WordUserAnswer? = userAnswers.find {
                it.id == questionId
            }

            val score = AnswerScore.Companion.reviewUserAnswer(
                difficulty = difficulty,
                expectedAnswer = expectedAnswer,
                userAnswer = userAnswer?.word
            )

            return@map GameReviewService.Companion.ReviewedQuestion(
                questionId = questionId,
                properAnswer = expectedAnswer,
                userAnswerScore = score
            )
        }.toSet()
    }

    override fun updateDBPointsForManyWords(
        user: User,
        language: LanguageName,
        reviewedQuestions: Set<GameReviewService.Companion.ReviewedQuestion>
    ) {
        val wordsToSave: MutableSet<Word> = mutableSetOf()
        val userActivityLogsToSave: MutableSet<UserActivityLog> = mutableSetOf()

        wordRepository.findAllWordByTheirOrigins(
            origins = reviewedQuestions.map { it.properAnswer }.toSet(),
            language = language,
            userId = user.id
        ).forEach { word ->
            val points: AnswerScore = reviewedQuestions.find { it.properAnswer == word.origin }?.userAnswerScore
                ?: return@forEach

            val isWordCompletedBefore: Boolean = word.isCompleted

            word.points += points.dbPoints
            word.isCompleted = word.points >= GamesConfig.Points.COMPLETE_WORD_THRESHOLD

            if (word.isCompleted && !isWordCompletedBefore) {
                word.completedAt = Instant.now()

                userActivityLogsToSave.add(
                    UserActivityLog(
                        user = user,
                        type = UserActivityType.WORD_COMPLETED,
                        language = language
                    )
                )
            }

            wordsToSave.add(word)
        }

        wordRepository.saveAll(wordsToSave)
        userActivityLogRepository.saveAll(userActivityLogsToSave)

        wordRepository.countWordsCompletedToday(language = language, userId = user.id).let {
            if (it >= 10) {
                userActivityLogRepository.save(
                    UserActivityLog(
                        user = user,
                        type = UserActivityType.WORDS_COMPLETED_IN_ONE_DAY_10,
                        language = language
                    )
                )
            }
        }

        wordRepository.countWordsCompletedInThisWeek(language = language, userId = user.id).let {
            if (it >= 30) {
                userActivityLogRepository.save(
                    UserActivityLog(
                        user = user,
                        type = UserActivityType.WORDS_COMPLETED_IN_ONE_WEEK_30,
                        language = language
                    )
                )
            }
        }
    }

}