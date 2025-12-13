package com.ord.core.word.api.ai.responses.dto

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_details.jsonb.ExampleSentence
import com.ord.core.word.models.word_details.jsonb.WordCollocation
import com.ord.core.word.models.word_details.jsonb.WordGrammar
import com.ord.core.word.models.word_details.jsonb.WordPronunciation

data class AIGeneratedWordManual(
    // Core Information
    var originalWord: String = "",
    val word: String,
    val translation: String,
    val definition: String,
    val type: WordType,
    val extraMark: WordExtraMark? = null,
    val useCases: List<String>,

    // Example Sentences
    val exampleSentences: List<ExampleSentence>,

    // Common Phrases & Collocations
    val collocations: List<WordCollocation>,

    // Pronunciation
    val pronunciation: WordPronunciation? = null,

    // Grammar Information
    val grammar: WordGrammar? = null,

    // Related Vocabulary
    val synonyms: List<String>,
    val antonyms: List<String>,

    // Learning Aids
    val commonMistakes: List<String>,
    val culturalNotes: String? = null,
    val learningTips: String? = null
)