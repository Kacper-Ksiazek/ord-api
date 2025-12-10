package com.ord.shared.prompts.structured_outputs.features.games

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val sentencesWritingGenerateSchema = StructuredOutputTemplate(
    name = "sentences_writing_generate",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "wordTopics" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "word" to mapOf(
                            "type" to "string",
                            "description" to "The word from the provided list"
                        ),
                        "topic" to mapOf(
                            "type" to "string",
                            "description" to "A topic that encourages the user to write a sentence using this word"
                        )
                    ),
                    "required" to listOf("word", "topic"),
                    "additionalProperties" to false,
                    "description" to "A word-topic pair for the sentences writing game"
                ),
                "description" to "Array of word-topic pairs for the sentences writing game"
            )
        ),
        "required" to listOf("wordTopics"),
        "additionalProperties" to false
    )
)
