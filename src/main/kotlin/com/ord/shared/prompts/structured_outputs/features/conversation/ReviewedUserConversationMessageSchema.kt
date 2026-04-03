package com.ord.shared.prompts.structured_outputs.features.conversation

import com.ord.features.conversation.models.conversation_user_message_analysis.enums.ConversationMessageErrorType
import com.ord.features.conversation.models.conversation_user_message_analysis.enums.ConversationMessageMistakeSeverity
import com.ord.features.conversation.models.conversation_user_message_analysis.enums.ConversationMessageStrengthType
import com.ord.features.conversation.models.conversation_user_message_analysis.enums.ConversationMessageSuggestionType
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val reviewedUserConversationMessageSchema = StructuredOutputTemplate(
    name = "conversation_review",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "sabotage" to mapOf(
                "type" to "string",
                "description" to "Detects sabotage: wrong language, extremely short answers (\"yes\", \"no\"), extremely offensive content, completely off-topic responses, gibberish/Lorem Ipsum/random characters, machine-generated nonsense, or repetitive junk. Return string with reason, rate everything as 0. Otherwise empty string."
            ),
            "correctedMessage" to mapOf(
                "type" to "string",
                "description" to "The user's message rewritten correctly, without any language mistakes. Empty string if the message has no mistakes and needs no correction."
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
            "mistakes" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "The SMALLEST possible exact quote from the user message — only the specific word or short phrase that is wrong. Never quote the full sentence unless the entire sentence structure is the mistake."
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
                            "description" to "Type of learning suggestion. VOCABULARY: alternative word choices for learning. STRUCTURE: alternative sentence structures for learning. NOT for error correction - use mistakes field for errors."
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
            "correctedMessage",
            "tutorComment",
            "grammar",
            "vocabulary",
            "naturalness",
            "coherenceWithContext",
            "mistakes",
            "strengths",
            "suggestions"
        ),
        "additionalProperties" to false
    )
)
