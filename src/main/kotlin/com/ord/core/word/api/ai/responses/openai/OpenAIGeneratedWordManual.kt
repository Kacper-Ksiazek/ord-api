package com.ord.core.word.api.ai.responses.openai

import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_details.jsonb.ExampleSentence
import com.ord.core.word.models.word_details.jsonb.WordCollocation
import com.ord.core.word.models.word_details.jsonb.WordConjugation
import com.ord.core.word.models.word_details.jsonb.WordGrammar
import com.ord.core.word.models.word_details.jsonb.WordPronunciation
import com.ord.core.word.models.word_details.enums.WordCollocationFrequency
import com.ord.core.word.models.word_details.enums.WordGender

/**
 * Intermediate DTO for OpenAI structured outputs response.
 *
 * This DTO matches OpenAI's structured output schema where all fields are required.
 * Nullable fields use empty string ("") or empty objects to represent null values,
 * which are then mapped to proper nulls in the domain model via `toDomain()`.
 *
 * Use `toDomain()` to convert this to `AIGeneratedWordManual` for application use.
 */
data class OpenAIGeneratedWordManual(
    val word: String,  // The actual word (corrected spelling if input was wrong)
    val translation: String,
    val definition: String,
    val type: WordType,
    val extraMark: String,  // Empty string if not applicable, otherwise enum value
    val useCases: List<String>,
    val exampleSentences: List<OpenAIExampleSentence>,
    val collocations: List<WordCollocation>,
    val pronunciation: OpenAIPronunciation,  // Use empty object for null
    val grammar: OpenAIGrammar,  // Use empty object for null
    val synonyms: List<String>,
    val antonyms: List<String>,
    val commonMistakes: List<String>,
    val culturalNotes: String,  // Empty string if not applicable
    val learningTips: String  // Empty string if not applicable
) {
    /**
     * Maps OpenAI response to domain model.
     * Converts empty strings and "null" strings to nulls and handles nested object mapping.
     */
    fun toDomain(inputWord: String): AIGeneratedWordManual {
        return AIGeneratedWordManual(
            originalWord = inputWord,
            word = word,
            translation = translation,
            definition = definition,
            type = type,
            extraMark = extraMark.toNullIfEmpty()?.let { WordExtraMark.valueOf(it) },
            useCases = useCases,
            exampleSentences = exampleSentences.map { it.toDomain() },
            collocations = collocations,
            pronunciation = pronunciation.toDomain(),
            grammar = grammar.toDomain(),
            synonyms = synonyms,
            antonyms = antonyms,
            commonMistakes = commonMistakes,
            culturalNotes = culturalNotes.toNullIfEmpty(),
            learningTips = learningTips.toNullIfEmpty()
        )
    }

    companion object {
        /**
         * Converts various empty-like values to null.
         * OpenAI sometimes returns punctuation marks or "null" variations to represent null values.
         */
        private fun String.toNullIfEmpty(): String? {
            val trimmed = this.trim()
            return when {
                trimmed.isEmpty() -> null
                // Check if the string contains "null" (case insensitive)
                trimmed.contains("null", ignoreCase = true) -> null
                trimmed.equals("n/a", ignoreCase = true) -> null
                trimmed.equals("none", ignoreCase = true) -> null
                // Check if string is only punctuation and/or whitespace
                trimmed.all { it in ",.;:!?-_/\\|*#@$%^&()[]{}\"'`~+=<> \t\n\r" } -> null
                else -> this
            }
        }
    }
}

/**
 * Intermediate version of ExampleSentence for OpenAI structured outputs.
 * The `context` field is required (uses empty string for null).
 */
data class OpenAIExampleSentence(
    val context: String,  // Empty string if not applicable
    val sentence: String,
    val translation: String
) {
    fun toDomain(): ExampleSentence {
        return ExampleSentence(
            context = context.toNullIfEmpty(),
            sentence = sentence,
            translation = translation
        )
    }

    private fun String.toNullIfEmpty(): String? {
        val trimmed = this.trim()
        return when {
            trimmed.isEmpty() -> null
            // Check if the string contains "null" (case insensitive)
            trimmed.contains("null", ignoreCase = true) -> null
            trimmed.equals("n/a", ignoreCase = true) -> null
            trimmed.equals("none", ignoreCase = true) -> null
            // Check if string is only punctuation and/or whitespace
            trimmed.all { it in ",.;:!?-_/\\|*#@$%^&()[]{}\"'`~+=<> \t\n\r" } -> null
            else -> this
        }
    }
}

/**
 * Intermediate version of WordPronunciation for OpenAI structured outputs.
 * All fields are required, using empty strings and 0 for null values.
 */
data class OpenAIPronunciation(
    val ipa: String,  // Empty string represents null pronunciation object
    val syllables: String,  // Empty string if not applicable
    val stress: Int  // 0 if not applicable
) {
    fun toDomain(): WordPronunciation? {
        // If ipa is empty-like, the entire pronunciation object is null
        val cleanedIpa = ipa.toNullIfEmpty()
        if (cleanedIpa == null) return null

        return WordPronunciation(
            ipa = cleanedIpa,
            syllables = syllables.toNullIfEmpty(),
            stress = if (stress == 0) null else stress
        )
    }

    private fun String.toNullIfEmpty(): String? {
        val trimmed = this.trim()
        return when {
            trimmed.isEmpty() -> null
            // Check if the string contains "null" (case insensitive)
            trimmed.contains("null", ignoreCase = true) -> null
            trimmed.equals("n/a", ignoreCase = true) -> null
            trimmed.equals("none", ignoreCase = true) -> null
            // Check if string is only punctuation and/or whitespace
            trimmed.all { it in ",.;:!?-_/\\|*#@$%^&()[]{}\"'`~+=<> \t\n\r" } -> null
            else -> this
        }
    }
}

/**
 * Intermediate version of WordGrammar for OpenAI structured outputs.
 * All fields are required, using empty strings and empty lists for null values.
 */
data class OpenAIGrammar(
    val gender: String,  // Empty string if not applicable
    val definiteArticle: String,  // Empty string if not applicable
    val pluralForm: String,  // Empty string if not applicable
    val comparativeForm: String,  // Empty string if not applicable
    val superlativeForm: String,  // Empty string if not applicable
    val irregularForms: Map<String, String>,  // Empty map if not applicable
    val conjugations: List<WordConjugation>  // Empty list if not applicable
) {
    fun toDomain(): WordGrammar? {
        // Clean up all string fields
        val cleanedGender = gender.toNullIfEmpty()
        val cleanedDefiniteArticle = definiteArticle.toNullIfEmpty()
        val cleanedPluralForm = pluralForm.toNullIfEmpty()
        val cleanedComparativeForm = comparativeForm.toNullIfEmpty()
        val cleanedSuperlativeForm = superlativeForm.toNullIfEmpty()

        // If all fields are empty, the entire grammar object is null
        val hasContent = cleanedGender != null ||
                        cleanedDefiniteArticle != null ||
                        cleanedPluralForm != null ||
                        cleanedComparativeForm != null ||
                        cleanedSuperlativeForm != null ||
                        irregularForms.isNotEmpty() ||
                        conjugations.isNotEmpty()

        if (!hasContent) return null

        return WordGrammar(
            gender = cleanedGender?.let { WordGender.valueOf(it) },
            definiteArticle = cleanedDefiniteArticle,
            pluralForm = cleanedPluralForm,
            comparativeForm = cleanedComparativeForm,
            superlativeForm = cleanedSuperlativeForm,
            irregularForms = irregularForms.ifEmpty { null },
            conjugations = conjugations.ifEmpty { null }
        )
    }

    private fun String.toNullIfEmpty(): String? {
        val trimmed = this.trim()
        return when {
            trimmed.isEmpty() -> null
            // Check if the string contains "null" (case insensitive)
            trimmed.contains("null", ignoreCase = true) -> null
            trimmed.equals("n/a", ignoreCase = true) -> null
            trimmed.equals("none", ignoreCase = true) -> null
            // Check if string is only punctuation and/or whitespace
            trimmed.all { it in ",.;:!?-_/\\|*#@$%^&()[]{}\"'`~+=<> \t\n\r" } -> null
            else -> this
        }
    }
}