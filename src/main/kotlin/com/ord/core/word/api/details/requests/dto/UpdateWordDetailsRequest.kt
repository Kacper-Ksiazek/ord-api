package com.ord.core.word.api.details.requests.dto

import com.ord.core.word.models.word_details.jsonb.ExampleSentence
import com.ord.core.word.models.word_details.jsonb.WordCollocation
import com.ord.core.word.models.word_details.jsonb.WordGrammar
import com.ord.core.word.models.word_details.jsonb.WordPronunciation
import org.openapitools.jackson.nullable.JsonNullable

data class UpdateWordDetailsRequest(
    val useCases: JsonNullable<Set<String>>,
    val synonyms: JsonNullable<Set<String>>,
    val antonyms: JsonNullable<Set<String>>,
    val commonMistakes: JsonNullable<Set<String>>,

    val exampleSentences: JsonNullable<Set<ExampleSentence>>,
    val collocations: JsonNullable<Set<WordCollocation>>,
    val pronunciation: JsonNullable<WordPronunciation?>,
    val grammar: JsonNullable<WordGrammar?>,

    val culturalNotes: JsonNullable<String?>,
    val learningTips: JsonNullable<String?>,
)
