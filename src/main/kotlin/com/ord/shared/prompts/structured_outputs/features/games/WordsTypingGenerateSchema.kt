package com.ord.shared.prompts.structured_outputs.features.games

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val wordsTypingGenerateSchema = StructuredOutputTemplate(
    name = "words_typing_generate",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "wordPairs" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "word" to mapOf(
                            "type" to "string",
                            "description" to "The word from the provided list"
                        ),
                        "clue" to mapOf(
                            "type" to "string",
                            "description" to "A clear and concise hint that helps the user guess the word. Do not include the word itself in the clue."
                        )
                    ),
                    "required" to listOf("word", "clue"),
                    "additionalProperties" to false,
                    "description" to "A word-clue pair for the typing game"
                ),
                "description" to "Array of word-clue pairs"
            )
        ),
        "required" to listOf("wordPairs"),
        "additionalProperties" to false
    )
)
