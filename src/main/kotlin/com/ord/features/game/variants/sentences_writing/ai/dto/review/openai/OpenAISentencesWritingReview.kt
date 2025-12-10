package com.ord.features.game.variants.sentences_writing.ai.dto.review.openai

import com.ord.features.game.variants.sentences_writing.ai.dto.review.AIReviewedSingleTopic
import com.ord.features.game.variants.sentences_writing.ai.dto.review.SentencesWritingEvaluationCriteria
import com.ord.shared.domain.dto.ScoringCriteria
import java.util.UUID

/**
 * Intermediate DTO for OpenAI structured outputs response for sentences writing game review.
 *
 * This DTO matches OpenAI's structured output schema where all fields are required.
 * OpenAI requires the root to be an object, so the array is wrapped in a "reviews" property.
 * Nullable fields use empty string ("") to represent null values, which are then
 * mapped to proper nulls in the domain model via `toDomain()`.
 *
 * Use `toDomain()` to convert this to `AIReviewedSentencesWritingGame` (List<AIReviewedSingleTopic>) for application use.
 */
data class OpenAISentencesWritingReview(
    val reviews: List<OpenAIReviewedSingleTopic>
)

/**
 * Intermediate version of AIReviewedSingleTopic for OpenAI structured outputs.
 * The `suggestedCorrectAnswer` field is required (uses empty string for null).
 */
data class OpenAIReviewedSingleTopic(
    val topicId: UUID,
    val evaluationCriteria: OpenAISentencesWritingEvaluationCriteria,
    val suggestedCorrectAnswer: String  // Empty string if user answer is 100% correct
) {
    fun toDomain(): AIReviewedSingleTopic {
        return AIReviewedSingleTopic(
            topicId = topicId,
            evaluationCriteria = evaluationCriteria.toDomain(),
            suggestedCorrectAnswer = suggestedCorrectAnswer.ifEmpty { null }
        )
    }
}

/**
 * Intermediate version of SentencesWritingEvaluationCriteria for OpenAI structured outputs.
 * All ScoringCriteria fields use intermediate type that handles nullable comments.
 */
data class OpenAISentencesWritingEvaluationCriteria(
    val fitsTopic: Boolean,
    val vocabulary: OpenAIScoringCriteria,
    val answerLength: OpenAIScoringCriteria,
    val correctWordUsage: OpenAIScoringCriteria
) {
    fun toDomain(): SentencesWritingEvaluationCriteria {
        return SentencesWritingEvaluationCriteria(
            fitsTopic = fitsTopic,
            vocabulary = vocabulary.toDomain(),
            answerLength = answerLength.toDomain(),
            correctWordUsage = correctWordUsage.toDomain()
        )
    }
}

/**
 * Intermediate version of ScoringCriteria for OpenAI structured outputs.
 * The `comment` field is required (uses empty string for null).
 */
data class OpenAIScoringCriteria(
    val score: Int,  // 0-10
    val comment: String  // Empty string if not needed
) {
    fun toDomain(): ScoringCriteria {
        return ScoringCriteria(
            score = score,
            comment = comment.ifEmpty { null }
        )
    }
}

/**
 * Extension function to convert the entire OpenAI response to domain model.
 */
fun OpenAISentencesWritingReview.toDomain(): List<AIReviewedSingleTopic> {
    return this.reviews.map { it.toDomain() }
}
