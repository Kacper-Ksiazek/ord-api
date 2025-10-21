package com.ord.core.word.api.details.requests.dto

import com.ord.core.word.models.word_details.jsonb.ExampleSentence
import com.ord.core.word.models.word_details.jsonb.WordCollocation
import com.ord.core.word.models.word_details.jsonb.WordGrammar
import com.ord.core.word.models.word_details.jsonb.WordPronunciation
import java.util.Optional

data class UpdateWordDetailsRequest(
    val useCases: Optional<Set<String>>,
    val synonyms: Optional<Set<String>>,
    val antonyms: Optional<Set<String>>,
    val commonMistakes: Optional<Set<String>>,

    val exampleSentences: Optional<Set<ExampleSentence>>,
    val collocations: Optional<Set<WordCollocation>>,
    val pronunciation: Optional<WordPronunciation?>,
    val grammar: Optional<WordGrammar?>,

    val culturalNotes: Optional<String?>,
    val learningTips: Optional<String?>,
)
