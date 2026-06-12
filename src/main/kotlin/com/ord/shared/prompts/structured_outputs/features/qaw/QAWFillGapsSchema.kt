package com.ord.shared.prompts.structured_outputs.features.qaw

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val qawFillGapsSchema = StructuredOutputTemplate(
    name = "qaw_fill_gaps",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "items" to mapOf(
                "type" to "array",
                "description" to "One enrichment result per input word, in the same order as the numbered input list",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "inputWord" to mapOf(
                            "type" to "string",
                            "description" to "The exact word string from the input list for this position",
                        ),
                        "word" to mapOf(
                            "type" to "string",
                            "description" to "Corrected spelling in the target language, or empty string if error is set",
                        ),
                        "translation" to mapOf(
                            "type" to "string",
                            "description" to "Translation into the desired language, or empty string if error is set",
                        ),
                        "definition" to mapOf(
                            "type" to "string",
                            "description" to "1-2 concise explanatory sentences in the generative content language, or empty string if error is set",
                        ),
                        "type" to mapOf(
                            "type" to "string",
                            "description" to "Word type: one of ${WordType.entries.joinToString(", ")}; empty string if error is set",
                        ),
                        "extraMark" to mapOf(
                            "type" to "string",
                            "description" to "Optional mark: one of ${WordExtraMark.entries.joinToString(", ")} when applicable; empty string otherwise or if error is set",
                        ),
                        "error" to mapOf(
                            "type" to "string",
                            "description" to "Error code when the word cannot be enriched (e.g. NON_EXISTENT_WORD, AMBIGUOUS_WORD); empty string on success",
                        ),
                    ),
                    "required" to listOf(
                        "inputWord",
                        "word",
                        "translation",
                        "definition",
                        "type",
                        "extraMark",
                        "error",
                    ),
                    "additionalProperties" to false,
                ),
            ),
        ),
        "required" to listOf("items"),
        "additionalProperties" to false,
    ),
)
