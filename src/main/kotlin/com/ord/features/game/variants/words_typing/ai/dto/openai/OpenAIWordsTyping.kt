package com.ord.features.game.variants.words_typing.ai.dto.openai

import com.ord.features.game.variants.words_typing.ai.dto.AIGeneratedWordsTypingData

/**
 * Intermediate DTO for OpenAI structured outputs response for words typing game generation.
 *
 * This DTO wraps the word-clue pairs in an object structure since OpenAI requires
 * the root schema to be an object (not a map or array). The wordPairs are then
 * converted to a Map<String, String> in the domain model via `toDomain()`.
 *
 * Use `toDomain()` to convert this to `AIGeneratedWordsTypingData` (Map<String, String>)
 * for application use.
 */
data class OpenAIWordsTyping(
    val wordPairs: List<OpenAIWordPair>
)

/**
 * Represents a single word-clue pair in the words typing game.
 */
data class OpenAIWordPair(
    val word: String,
    val clue: String
)

/**
 * Extension function to convert the entire OpenAI response to domain model.
 * Transforms the array of word-clue pairs into a Map<String, String>.
 */
fun OpenAIWordsTyping.toDomain(): AIGeneratedWordsTypingData {
    return wordPairs.associate { it.word to it.clue }
}
