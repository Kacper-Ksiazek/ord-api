package com.ord.features.game.variants.sentences_writing.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.config.GamesConfig
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageOperationType
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingSentencesWritingGameDTO
import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import com.ord.features.game.variants.sentences_writing.ai.dto.review.AIReviewedSentencesWritingGame
import com.ord.features.game.variants.sentences_writing.ai.dto.review.openai.OpenAISentencesWritingReview
import com.ord.features.game.variants.sentences_writing.ai.dto.review.openai.toDomain
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameAnswers
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.ReviewedSentencesWritingSingleTopic
import com.ord.features.game.variants.shared.ai.AIGameServiceBase
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class SentencesWritingAIReviewService(
    private val languageProficiencyService: LanguageProficiencyService,
) : AIGameServiceBase() {
    fun review(
        userId: UUID,
        ongoingGame: OngoingSentencesWritingGameDTO,
        userAnswers: FinishSentencesWritingGameAnswers
    ): Mono<FinishedSentencesWritingGameResponse> {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(userId, ongoingGame.language)
            .flatMap { languageProficiency ->
                val prompt = Prompt(
                    variant = AvailablePrompts.GAMES_REVIEW_SENTENCES_WRITING,
                    params = mapOf(
                        "language" to ongoingGame.language.toString(),
                        "difficulty" to ongoingGame.difficulty.toString(),
                        "proficiency" to languageProficiency.level.toString(),
                        "serializedAnswers" to serializeUserAnswers(
                            userAnswers = userAnswers,
                            instruction = ongoingGame.properAnswers
                        ),
                    )
                )

                makeGameAIRequest(
                    userId = userId,
                    operationType = GptTokensUsageOperationType.Game.Review.SENTENCES_WRITING,
                    aiResponseTypeReference = object : TypeReference<OpenAISentencesWritingReview>() {},
                    prompt = prompt.toString(),
                    structuredOutput = prompt.variant.structuredOutput,
                    validateResponseBody = { parsedResponse ->
                        val expectedTopicsIds = ongoingGame.properAnswers.map { it.id }

                        parsedResponse is OpenAISentencesWritingReview &&
                                parsedResponse.reviews.size == ongoingGame.properAnswers.size &&
                                parsedResponse.reviews.all { it.topicId in expectedTopicsIds }
                    }
                )
                    .map { it.toDomain() }
            }
            .map { aiReviewedGame ->
                val (totalScore, maxScorePerTopic, scoringPerTopic) = computeScoring(aiReviewedGame)

                val reviewedAnswers: Set<ReviewedSentencesWritingSingleTopic> = aiReviewedGame.map { reviewedTopic ->
                    val topic = ongoingGame.properAnswers.firstOrNull { it.id == reviewedTopic.topicId }!!

                    ReviewedSentencesWritingSingleTopic(
                        id = reviewedTopic.topicId,
                        topic = topic.topic,
                        word = topic.word,
                        evaluationCriteria = reviewedTopic.evaluationCriteria,
                        score = scoringPerTopic[reviewedTopic.topicId] ?: 0,
                        maxScore = maxScorePerTopic,
                        suggestedCorrectAnswer = reviewedTopic.suggestedCorrectAnswer
                    )
                }.toSet()

                FinishedSentencesWritingGameResponse(
                    score = totalScore,
                    maxScore = GamesConfig.GameScoring.MaxScore.SENTENCES_WRITING,
                    reviewedAnswers = reviewedAnswers
                )
            }
    }

    private fun serializeUserAnswers(
        userAnswers: Map<UUID, String>,
        instruction: SentencesWritingProperAnswers
    ): String {
        return instruction.joinToString("\n") { topic ->
            val correspondingUserAnswer: String = userAnswers[topic.id] ?: "❌ no answer provided"

            """
            Topic ID: ${topic.id}.
            - theme: ${topic.topic}
            - word: ${topic.word}
            - user's answer: $correspondingUserAnswer
            """
        }
    }

    /**
     * Computes the scoring for the AI-reviewed sentences writing game.
     *
     * @param aiReviewedGame The list of AI-reviewed answers for each topic.
     * @return A Triple containing:
     *   - **Int**: The total maximum possible score for the entire game.
     *   - **Int**: The maximum possible score per topic.
     *   - **Map<UUID, Int>**: A map where each key is a topic ID and the value is the calculated score for that topic.
     */
    private fun computeScoring(
        aiReviewedGame: AIReviewedSentencesWritingGame,
    ): Triple<
            Int,
            Int,
            Map<UUID, Int>
            > {
        val weights = GamesConfig.GameScoring.ModulesWeights.SentencesWriting
        val maxScoringPerTopic = GamesConfig.GameScoring.MaxScore.SENTENCES_WRITING / aiReviewedGame.size;

        val scoringPerTopic = aiReviewedGame.associate {
            if (!it.evaluationCriteria.fitsTopic || it.evaluationCriteria.correctWordUsage.score == 0) {
                return@associate it.topicId to 0
            }

            val lengthRating = it.evaluationCriteria.answerLength.score.toDouble() / 10.0;
            val vocabularyRating = it.evaluationCriteria.vocabulary.score.toDouble() / 10.0;
            val wordUsageRating = it.evaluationCriteria.correctWordUsage.score.toDouble() / 10.0;

            // [0.0 - 1.0]
            val totalRating = weights.ANSWER_LENGTH * lengthRating +
                    weights.VOCABULARY * vocabularyRating +
                    weights.CORRECT_WORD_USAGE * wordUsageRating;

            val score = (totalRating * maxScoringPerTopic).toInt();

            it.topicId to score
        }

        val totalScore = scoringPerTopic.values.sum()

        return Triple(
            totalScore,
            maxScoringPerTopic,
            scoringPerTopic,
        )
    }
}