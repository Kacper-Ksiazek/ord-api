package com.ord.features.game.variants.sentences_writing.ai.dto.generate.openai

import com.ord.features.game.variants.sentences_writing.ai.dto.generate.AIGeneratedSentencesWritingGame

/**
 * Intermediate DTO for OpenAI structured outputs response for sentences writing game generation.
 *
 * This DTO wraps the word-topic pairs in an object structure since OpenAI requires
 * the root schema to be an object (not a map). The wordTopics are then
 * converted to a Map<String, String> in the domain model via `toDomain()`.
 *
 * Use `toDomain()` to convert this to `AIGeneratedSentencesWritingGame` (Map<String, String>)
 * for application use.
 */
data class OpenAISentencesWritingGame(
    val wordTopics: List<OpenAIWordTopic>
)

/**
 * Represents a single word-topic pair in the sentences writing game.
 */
data class OpenAIWordTopic(
    val word: String,
    val topic: String
)

/**
 * Extension function to convert the entire OpenAI response to domain model.
 * Transforms the array of word-topic pairs into a Map<String, String>.
 */
fun OpenAISentencesWritingGame.toDomain(): AIGeneratedSentencesWritingGame {
    return wordTopics.associate { it.word to it.topic }
}
