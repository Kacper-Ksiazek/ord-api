package com.ord.core.word.api.responses.dto

import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordFrequency
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.core.word.model.json.WordCollocation
import com.ord.core.word.model.json.WordGrammar
import com.ord.core.word.model.json.WordPronunciation

data class AIGeneratedWordManual(
    // Core Information
    var originalWord: String = "",
    val translation: String,
    val definition: String,
    val type: WordType,
    val extraMark: WordExtraMark? = null,
    val difficultyScore: Int,
    val useCases: List<String>,
    val everydayUsageFrequency: WordFrequency,

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