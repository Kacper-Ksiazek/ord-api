package com.ord.shared.prompts.structured_outputs.features.conversation

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val aiMessageLearningTipsSchema = StructuredOutputTemplate(
    name = "ai_message_learning_tips",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "grammarTips" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "Exact quote from AI message (in target language)"
                        ),
                        "explanation" to mapOf(
                            "type" to "string",
                            "description" to "Grammar explanation (in generativeContentLanguage)"
                        ),
                        "grammarPoint" to mapOf(
                            "type" to "string",
                            "description" to "Specific grammar structure (e.g., 'Past Perfect', 'Subjunctive')"
                        )
                    ),
                    "required" to listOf("phrase", "explanation", "grammarPoint"),
                    "additionalProperties" to false
                ),
                "description" to "0-2 grammar-focused tips. Empty array if none applicable."
            ),
            "vocabularyTips" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "word" to mapOf(
                            "type" to "string",
                            "description" to "Word or phrase from AI message (in target language)"
                        ),
                        "definition" to mapOf(
                            "type" to "string",
                            "description" to "Clear definition (in generativeContentLanguage)"
                        ),
                        "usageNote" to mapOf(
                            "type" to "string",
                            "description" to "When/how to use (in generativeContentLanguage)"
                        ),
                        "proficiencyLevel" to mapOf(
                            "type" to "string",
                            "description" to "Level indicator (A1, A2, B1, B2, C1, C2)"
                        )
                    ),
                    "required" to listOf("word", "definition", "usageNote", "proficiencyLevel"),
                    "additionalProperties" to false
                ),
                "description" to "0-2 vocabulary tips. Empty array if none applicable."
            ),
            "idiomTips" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "Exact idiomatic phrase from AI message (in target language)"
                        ),
                        "meaning" to mapOf(
                            "type" to "string",
                            "description" to "Literal and figurative meaning (in generativeContentLanguage)"
                        ),
                        "example" to mapOf(
                            "type" to "string",
                            "description" to "Usage example in different context (in target language)"
                        )
                    ),
                    "required" to listOf("phrase", "meaning", "example"),
                    "additionalProperties" to false
                ),
                "description" to "0-1 idiom tips. Empty array if none applicable."
            ),
            "culturalTips" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phrase" to mapOf(
                            "type" to "string",
                            "description" to "Culture-relevant phrase from AI message (in target language)"
                        ),
                        "culturalContext" to mapOf(
                            "type" to "string",
                            "description" to "Cultural significance explanation (in generativeContentLanguage)"
                        ),
                        "regionalNote" to mapOf(
                            "type" to "string",
                            "description" to "Regional variations. Empty string if not applicable."
                        )
                    ),
                    "required" to listOf("phrase", "culturalContext", "regionalNote"),
                    "additionalProperties" to false
                ),
                "description" to "0-1 cultural tips. Empty array if none applicable."
            )
        ),
        "required" to listOf(
            "grammarTips",
            "vocabularyTips",
            "idiomTips",
            "culturalTips"
        ),
        "additionalProperties" to false
    )
)
