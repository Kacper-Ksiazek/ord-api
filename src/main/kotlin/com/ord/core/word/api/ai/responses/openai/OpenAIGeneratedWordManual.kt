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
    val originalWord: String,
    val suggestedCorrection: String,  // Empty string if no correction needed
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
     * Converts empty strings to nulls and handles nested object mapping.
     */
    fun toDomain(word: String): AIGeneratedWordManual {
        return AIGeneratedWordManual(
            originalWord = word,
            suggestedCorrection = suggestedCorrection.ifEmpty { null },
            translation = translation,
            definition = definition,
            type = type,
            extraMark = if (extraMark.isEmpty()) null else WordExtraMark.valueOf(extraMark),
            useCases = useCases,
            exampleSentences = exampleSentences.map { it.toDomain() },
            collocations = collocations,
            pronunciation = pronunciation.toDomain(),
            grammar = grammar.toDomain(),
            synonyms = synonyms,
            antonyms = antonyms,
            commonMistakes = commonMistakes,
            culturalNotes = culturalNotes.ifEmpty { null },
            learningTips = learningTips.ifEmpty { null }
        )
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
            context = context.ifEmpty { null },
            sentence = sentence,
            translation = translation
        )
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
        // If ipa is empty, the entire pronunciation object is null
        if (ipa.isEmpty()) return null

        return WordPronunciation(
            ipa = ipa,
            syllables = syllables.ifEmpty { null },
            stress = if (stress == 0) null else stress
        )
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
        // If all fields are empty, the entire grammar object is null
        val hasContent = gender.isNotEmpty() ||
                        definiteArticle.isNotEmpty() ||
                        pluralForm.isNotEmpty() ||
                        comparativeForm.isNotEmpty() ||
                        superlativeForm.isNotEmpty() ||
                        irregularForms.isNotEmpty() ||
                        conjugations.isNotEmpty()

        if (!hasContent) return null

        return WordGrammar(
            gender = if (gender.isEmpty()) null else WordGender.valueOf(gender),
            definiteArticle = definiteArticle.ifEmpty { null },
            pluralForm = pluralForm.ifEmpty { null },
            comparativeForm = comparativeForm.ifEmpty { null },
            superlativeForm = superlativeForm.ifEmpty { null },
            irregularForms = irregularForms.ifEmpty { null },
            conjugations = conjugations.ifEmpty { null }
        )
    }
}