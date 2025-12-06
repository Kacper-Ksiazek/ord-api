package com.ord.shared.prompts.structured_outputs.features.conversation

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ErrorType
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val reviewedUserConversationMessageSchema = StructuredOutputTemplate(
    name = "conversation_review",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "sabotage" to mapOf(
                "type" to "string",
                "description" to "Detects sabotage: wrong language, extremely short answers (\"yes\", \"no\"), extremely offensive content, or completely off-topic responses. Return string with reason, rate everything as 0. Otherwise empty string."
            ),
            "grammar" to mapOf(
                "type" to "integer",
                "minimum" to 0,
                "maximum" to 10,
                "description" to "Accuracy of grammatical structures (0-10 inclusive)"
            ),
            "vocabulary" to mapOf(
                "type" to "integer",
                "minimum" to 0,
                "maximum" to 10,
                "description" to "Appropriateness, accuracy, and range of word choice (0-10 inclusive)"
            ),
            "answerLength" to mapOf(
                "type" to "integer",
                "minimum" to 0,
                "maximum" to 10,
                "description" to "Adequacy relative to question asked and conversation type (0-10 inclusive)"
            ),
            "naturalness" to mapOf(
                "type" to "integer",
                "minimum" to 0,
                "maximum" to 10,
                "description" to "How native-like the expression is (0-10 inclusive)"
            ),
            "coherenceWithContext" to mapOf(
                "type" to "integer",
                "minimum" to 0,
                "maximum" to 10,
                "description" to "How well the message responds to the AI's previous message (0-10 inclusive)"
            ),
            "registerAppropriate" to mapOf(
                "type" to "boolean",
                "description" to "Does the formality level match the conversation type?"
            ),
            "mistakes" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "Exact quote from user message"
                        ),
                        "severity" to mapOf(
                            "type" to "integer",
                            "minimum" to 1,
                            "maximum" to 3,
                            "description" to "Severity level: 3 (Critical), 2 (Moderate), 1 (Minor)"
                        ),
                        "errorType" to mapOf(
                            "type" to "string",
                            "enum" to ErrorType.entries,
                            "description" to "Category for analytics"
                        ),
                        "explanation" to mapOf(
                            "type" to "string",
                            "description" to "Why it's wrong (in target language)"
                        ),
                        "correctForm" to mapOf(
                            "type" to "string",
                            "description" to "The correct version"
                        )
                    ),
                    "required" to listOf("phrase", "severity", "errorType", "explanation", "correctForm"),
                    "additionalProperties" to false
                ),
                "description" to "Empty array if perfect, order by severity (critical first)"
            ),
            "strengthsIdentified" to mapOf(
                "type" to "array",
                "items" to mapOf("type" to "string"),
                "description" to "Always try to find 2-3 positive points, empty if sabotage"
            ),
            "vocabularyEnrichment" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "original" to mapOf("type" to "string", "description" to "What user said"),
                        "suggestion" to mapOf("type" to "string", "description" to "More advanced alternative"),
                        "explanation" to mapOf("type" to "string", "description" to "Why suggestion is better")
                    ),
                    "required" to listOf("original", "suggestion", "explanation"),
                    "additionalProperties" to false
                ),
                "description" to "Only for B1+ levels. Empty array if not applicable"
            ),
            "alternativeExpressions" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "context" to mapOf(
                            "type" to "string",
                            "description" to "Which part of message this applies to"
                        ),
                        "alternatives" to mapOf(
                            "type" to "array",
                            "items" to mapOf("type" to "string"),
                            "description" to "Different ways to express the same idea"
                        ),
                        "note" to mapOf(
                            "type" to "string",
                            "description" to "Nuances between alternatives. Use empty string if not applicable."
                        )
                    ),
                    "required" to listOf("context", "alternatives", "note"),
                    "additionalProperties" to false
                ),
                "description" to "Help user level up. Empty array if not applicable"
            ),
            "culturalNote" to mapOf(
                "type" to "string",
                "description" to "Grammatically correct but culturally inappropriate usage. Empty string if fine."
            )
        ),
        "required" to listOf(
            "sabotage",
            "grammar",
            "vocabulary",
            "answerLength",
            "naturalness",
            "coherenceWithContext",
            "registerAppropriate",
            "mistakes",
            "strengthsIdentified",
            "vocabularyEnrichment",
            "alternativeExpressions",
            "culturalNote"
        ),
        "additionalProperties" to false
    )
)
