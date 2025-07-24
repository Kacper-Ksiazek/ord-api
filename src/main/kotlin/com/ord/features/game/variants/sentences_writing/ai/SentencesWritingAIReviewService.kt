package com.ord.features.game.variants.sentences_writing.ai

import com.ord.config.GamesConfig
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.OngoingSentencesWritingGameDTO
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import com.ord.features.game.services.OngoingGameService
import com.ord.features.game.variants.sentences_writing.ai.dto.review.AIReviewedSentencesWritingGame
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.ReviewedSentencesWritingSingleTopic
import com.ord.features.game.variants.shared.ai.AIGameServiceBase
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class SentencesWritingAIReviewService(
    private val languageProficiencyService: LanguageProficiencyService,
    private val ongoingGameService: OngoingGameService,
    private val ongoingGameMapper: OngoingGameMapper,
    private val serverProperties: ServerProperties,
) : AIGameServiceBase() {
    fun review(
        user: UserEntity,
        body: FinishSentencesWritingGameRequest
    ): ResponseEntity<FinishedSentencesWritingGameResponse> {
        val ongoingGame: OngoingSentencesWritingGameDTO = ongoingGameMapper.toSentencesWritingDTO(
            ongoingGameService.findByIdOrFail(body.gameId, user.id)
        )

        val languageProficiency: LanguageProficiencyEntity =
            languageProficiencyService.findUserProficiencyInLanguageOrThrow(user.id, ongoingGame.language)

        val prompt = Prompt(
            variant = AvailablePrompts.GAMES_REVIEW_SENTENCES_WRITING,
            params = mapOf(
                "language" to ongoingGame.language.toString(),
                "difficulty" to ongoingGame.difficulty.toString(),
                "proficiency" to languageProficiency.proficiency.toString(),
                "serializedAnswers" to serializeUserAnswers(
                    userAnswers = body.answers,
                    instruction = ongoingGame.properAnswers
                ),
            )
        )

        val aiReviewedGame: AIReviewedSentencesWritingGame = makeGameAIRequest(
            clazz = AIReviewedSentencesWritingGame::class.java,
            prompt = prompt.toString(),

            gameType = GameType.SENTENCES_WRITING,
            language = ongoingGame.language,
            difficulty = ongoingGame.difficulty,
            consumptionType = GamesGPTTokensConsumptionType.REVIEW,

            user = user,

            validateResponseBody = { parsedResponse ->
                val expectedTopicsIds = ongoingGame.properAnswers.map { it.id }

                parsedResponse is AIReviewedSentencesWritingGame &&
                        parsedResponse.size == ongoingGame.properAnswers.size &&
                        parsedResponse.all { it.topicId in expectedTopicsIds }
            }
        )

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

        val gameReviewUrl =
            "${serverProperties.servlet.contextPath}/api/v1/games/sentences-writing/review/${ongoingGame.id}"

        return ResponseEntity.ok(
            FinishedSentencesWritingGameResponse(
                score = totalScore,
                maxScore = GamesConfig.GameScoring.MaxScore.SENTENCES_WRITING,

                reviewedAnswers = reviewedAnswers,
            )
        )
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