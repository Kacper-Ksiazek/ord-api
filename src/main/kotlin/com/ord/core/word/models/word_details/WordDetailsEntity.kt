package com.ord.core.word.models.word_details

import com.ord.shared.models.IdentifiableUserResource
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("word_details")
data class WordDetailsEntity(
    @Id
    override val id: UUID? = null,

    val wordId: UUID,

    val useCases: Json, // JSONB - Set<String>
    val synonyms: Json, // JSONB - Set<String>
    val antonyms: Json, // JSONB - Set<String>
    val commonMistakes: Json, // JSONB - Set<String>

    val exampleSentences: Json, // JSONB - Set<ExampleSentence>
    val collocations: Json, // JSONB - Set<WordCollocation>
    val pronunciation: Json? = null, // JSONB - WordPronunciation
    val grammar: Json? = null, // JSONB - WordGrammar

    val culturalNotes: String? = null,
    val learningTips: String? = null,

    override val userId: UUID,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) : IdentifiableUserResource
