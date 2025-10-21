package com.ord.core.word.api.details.requests.dto

import com.ord.core.word.models.word_details.jsonb.ExampleSentence
import com.ord.core.word.models.word_details.jsonb.WordCollocation
import com.ord.core.word.models.word_details.jsonb.WordGrammar
import com.ord.core.word.models.word_details.jsonb.WordPronunciation

data class CreateWordDetailsRequest(
    val useCases: Set<String>,
    val synonyms: Set<String>,
    val antonyms: Set<String>,
    val commonMistakes: Set<String>,

    val exampleSentences: Set<ExampleSentence>,
    val collocations: Set<WordCollocation>,
    val pronunciation: WordPronunciation? = null,
    val grammar: WordGrammar? = null,

    val culturalNotes: String? = null,
    val learningTips: String? = null,
)
