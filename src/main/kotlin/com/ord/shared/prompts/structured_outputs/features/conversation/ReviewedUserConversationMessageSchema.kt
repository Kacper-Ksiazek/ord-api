package com.ord.shared.prompts.structured_outputs.features.conversation

import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageErrorType
import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageMistakeSeverity
import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageStrengthType
import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ConversationMessageSuggestionType
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
            "tutorComment" to mapOf(
                "type" to "string",
                "description" to "A short, personal comment (1-2 sentences) from the tutor in the user's chosen feedback language (generativeContentLanguage). Acknowledge progress, highlight key strengths or areas for improvement, and provide encouragement like a real language tutor would. This should complement other feedback without duplicating it."
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
                            "type" to "string",
                            "enum" to ConversationMessageMistakeSeverity.entries,
                            "description" to "Severity of the mistake. Use the provided severity descriptions as guidance."
                        ),
                        "errorType" to mapOf(
                            "type" to "string",
                            "enum" to ConversationMessageErrorType.entries,
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
            "strengths" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "Exact quote from user message demonstrating this strength"
                        ),
                        "strengthType" to mapOf(
                            "type" to "string",
                            "enum" to ConversationMessageStrengthType.entries,
                            "description" to "Category of linguistic strength"
                        ),
                        "explanation" to mapOf(
                            "type" to "string",
                            "description" to "Why this is good (in generativeContentLanguage)"
                        )
                    ),
                    "required" to listOf("phrase", "strengthType", "explanation"),
                    "additionalProperties" to false
                ),
                "description" to "Always try to find 2-3 positive points with specific examples, empty if sabotage"
            ),
            "suggestions" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "original" to mapOf(
                            "type" to "string",
                            "description" to "Exact phrase from user message"
                        ),
                        "suggestionType" to mapOf(
                            "type" to "string",
                            "enum" to ConversationMessageSuggestionType.entries,
                            "description" to "IMPROVEMENT: vocabulary/phrasing inadequate for context. ENRICHMENT: interesting alternatives to expand repertoire."
                        ),
                        "alternatives" to mapOf(
                            "type" to "array",
                            "items" to mapOf("type" to "string"),
                            "description" to "Better/different ways to express it (1 or more)"
                        ),
                        "explanation" to mapOf(
                            "type" to "string",
                            "description" to "Why these alternatives are better/useful (in generativeContentLanguage)"
                        )
                    ),
                    "required" to listOf("original", "suggestionType", "alternatives", "explanation"),
                    "additionalProperties" to false
                ),
                "description" to "Vocabulary and expression suggestions. Empty array if not applicable or sabotage."
            )
        ),
        "required" to listOf(
            "sabotage",
            "tutorComment",
            "grammar",
            "vocabulary",
            "answerLength",
            "naturalness",
            "coherenceWithContext",
            "registerAppropriate",
            "mistakes",
            "strengths",
            "suggestions"
        ),
        "additionalProperties" to false
    )
)
