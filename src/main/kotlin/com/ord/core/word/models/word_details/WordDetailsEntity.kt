package com.ord.core.word.models.word_details

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("word_details")
data class WordDetailsEntity(
    @Id
    val id: UUID? = null,

    val wordId: UUID,

    val useCases: String, // JSONB - Set<String>
    val synonyms: String, // JSONB - Set<String>
    val antonyms: String, // JSONB - Set<String>
    val commonMistakes: String, // JSONB - Set<String>

    val exampleSentences: String, // JSONB - Set<ExampleSentence>
    val collocations: String, // JSONB - Set<WordCollocation>
    val pronunciation: String? = null, // JSONB - WordPronunciation
    val grammar: String? = null, // JSONB - WordGrammar

    val culturalNotes: String? = null,
    val learningTips: String? = null,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
