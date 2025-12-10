package com.ord.shared.prompts.structured_outputs.features.games

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val crosswordGenerateSchema = StructuredOutputTemplate(
    name = "crossword_generate",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "answer" to mapOf(
                "type" to "string",
                "description" to "The main answer of the crossword - either a new word or a short phrase. Do not use a word from the list provided."
            ),
            "answerExplanation" to mapOf(
                "type" to "string",
                "description" to "Explanation of the answer. DO NOT include the answer in its explanation."
            ),
            "questions" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "word" to mapOf(
                            "type" to "string",
                            "description" to "Use words from the provided list. Each word can be used only once."
                        ),
                        "clue" to mapOf(
                            "type" to "string",
                            "description" to "DO NOT include the word in its clue."
                        )
                    ),
                    "required" to listOf("word", "clue"),
                    "additionalProperties" to false,
                    "description" to "A crossword question with word and clue"
                ),
                "description" to "List of crossword questions"
            )
        ),
        "required" to listOf("answer", "answerExplanation", "questions"),
        "additionalProperties" to false
    )
)
