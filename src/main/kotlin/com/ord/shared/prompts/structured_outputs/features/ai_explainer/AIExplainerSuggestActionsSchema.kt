package com.ord.shared.prompts.structured_outputs.features.ai_explainer

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

// Enum values are literal on purpose: AvailablePrompts (initialised by ExplainPhraseFollowUpAction) holds this schema,
// so referencing those enums here would create a class-initialisation cycle. ExplainPhraseKindTest keeps them in sync.
val aiExplainerSuggestActionsSchema = StructuredOutputTemplate(
    name = "ai_explainer_suggest_actions",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "kind" to mapOf(
                "type" to "string",
                "enum" to listOf("WORD_OR_PHRASE", "GRAMMAR_OR_CONCEPT"),
                "description" to "WORD_OR_PHRASE when the learner asked about a word, idiom or expression; GRAMMAR_OR_CONCEPT when they asked about a grammar structure, rule or language concept."
            ),
            "suggestedActions" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "string",
                    "enum" to listOf(
                        "SIMPLER", "MORE_EXAMPLES", "SIMILAR_EXPRESSIONS", "COMMON_MISTAKES", "USED_WITH", "IN_MY_LANGUAGE"
                    ),
                ),
                "description" to "Refine actions that fit this answer best, most useful first. Each at most once."
            ),
            "emoji" to mapOf(
                "type" to "string",
                "description" to "Exactly one emoji that best represents the topic of the question (e.g. a dog for 'hund', a clock for a tense). Never text."
            ),
        ),
        "required" to listOf("kind", "suggestedActions", "emoji"),
        "additionalProperties" to false
    )
)
