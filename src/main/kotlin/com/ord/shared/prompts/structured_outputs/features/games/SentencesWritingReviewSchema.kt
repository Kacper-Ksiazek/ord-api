package com.ord.shared.prompts.structured_outputs.features.games

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val sentencesWritingReviewSchema = StructuredOutputTemplate(
    name = "sentences_writing_review",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "reviews" to mapOf(
                "type" to "array",
                "items" to mapOf(
            "type" to "object",
            "properties" to mapOf(
                "topicId" to mapOf(
                    "type" to "string",
                    "description" to "UUID of the topic being reviewed"
                ),
                "evaluationCriteria" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "fitsTopic" to mapOf(
                            "type" to "boolean",
                            "description" to "Whether the sentence fits the given topic. Don't be too harsh - if related, set to true."
                        ),
                        "vocabulary" to mapOf(
                            "type" to "object",
                            "properties" to mapOf(
                                "score" to mapOf(
                                    "type" to "integer",
                                    "minimum" to 0,
                                    "maximum" to 10,
                                    "description" to "Vocabulary quality score (0-10 inclusive)"
                                ),
                                "comment" to mapOf(
                                    "type" to "string",
                                    "description" to "Optional clarification. Use empty string if not needed."
                                )
                            ),
                            "required" to listOf("score", "comment"),
                            "additionalProperties" to false,
                            "description" to "Vocabulary quality assessment"
                        ),
                        "answerLength" to mapOf(
                            "type" to "object",
                            "properties" to mapOf(
                                "score" to mapOf(
                                    "type" to "integer",
                                    "minimum" to 0,
                                    "maximum" to 10,
                                    "description" to "Answer length appropriateness score (0-10 inclusive)"
                                ),
                                "comment" to mapOf(
                                    "type" to "string",
                                    "description" to "Optional clarification. Use empty string if not needed."
                                )
                            ),
                            "required" to listOf("score", "comment"),
                            "additionalProperties" to false,
                            "description" to "Answer length assessment relative to difficulty"
                        ),
                        "correctWordUsage" to mapOf(
                            "type" to "object",
                            "properties" to mapOf(
                                "score" to mapOf(
                                    "type" to "integer",
                                    "minimum" to 0,
                                    "maximum" to 10,
                                    "description" to "Word usage correctness score (0-10 inclusive). If word not used at all, must be 0."
                                ),
                                "comment" to mapOf(
                                    "type" to "string",
                                    "description" to "Optional clarification. Use empty string if not needed."
                                )
                            ),
                            "required" to listOf("score", "comment"),
                            "additionalProperties" to false,
                            "description" to "Assessment of how correctly the requested word is used"
                        )
                    ),
                    "required" to listOf("fitsTopic", "vocabulary", "answerLength", "correctWordUsage"),
                    "additionalProperties" to false,
                    "description" to "Detailed evaluation criteria for the answer"
                ),
                "suggestedCorrectAnswer" to mapOf(
                    "type" to "string",
                    "description" to "Suggested correct answer if user's answer needs improvement. Use empty string if user answer is 100% correct."
                )
            ),
                    "required" to listOf("topicId", "evaluationCriteria", "suggestedCorrectAnswer"),
                    "additionalProperties" to false
                ),
                "description" to "Array of reviewed topics with evaluation criteria"
            )
        ),
        "required" to listOf("reviews"),
        "additionalProperties" to false
    )
)
