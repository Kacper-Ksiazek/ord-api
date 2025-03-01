package com.backend.ord.services.impl

import com.backend.ord.api.requests.games.data.WordUserAnswer
import com.backend.ord.config.GamesConfig
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.enums.application.game.AnswerScore
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.repositories.WordRepository
import com.backend.ord.services.GameReviewService
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameReviewServiceImpl(
    val wordRepository: WordRepository
) : GameReviewService {
    override fun reviewUserAnswers(
        expectedAnswers: Map<UUID, String>,
        userAnswers: Set<WordUserAnswer>,
        difficulty: GameDifficulty
    ): Set<GameReviewService.Companion.ReviewedQuestion> {
        return expectedAnswers.entries.map { (questionId, expectedAnswer) ->
            val score = AnswerScore.Companion.reviewUserAnswer(
                difficulty = difficulty,
                expectedAnswer = expectedAnswer,
                userAnswer = userAnswers.find {
                    it.id == questionId
                }?.word,
            )

            return@map GameReviewService.Companion.ReviewedQuestion(
                questionId = questionId,
                properAnswer = expectedAnswer,
                userAnswerScore = score
            )
        }.toSet()
    }

    override fun updateDBPointsForManyWords(
        userId: UUID,
        language: LanguageName,
        reviewedQuestions: Set<GameReviewService.Companion.ReviewedQuestion>
    ) {
        val wordsToSave: MutableSet<Word> = mutableSetOf()

        wordRepository.findAllWordByTheirOrigins(
            origins = reviewedQuestions.map { it.properAnswer }.toSet(),
            language = language,
            userId = userId
        ).forEach { word ->
            val points: AnswerScore = reviewedQuestions.find { it.properAnswer == word.origin }?.userAnswerScore
                ?: return@forEach

            word.points += points.dbPoints
            word.isCompleted = word.points >= GamesConfig.Points.COMPLETE_WORD_THRESHOLD

            wordsToSave.add(word)
        }

        wordRepository.saveAll(wordsToSave)
    }

}