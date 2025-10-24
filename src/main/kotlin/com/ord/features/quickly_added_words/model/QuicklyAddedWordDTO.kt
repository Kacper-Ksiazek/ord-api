package com.ord.features.quickly_added_words.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import java.time.Instant
import java.util.*

data class QuicklyAddedWordDTO(
    val id: UUID,

    var word: String,
    var language: LanguageName,

    val definition: String?,
    val extraMark: WordExtraMark?,
    val type: WordType?,

    val isApproved: Boolean,

    val userId: UUID,

    val createdAt: Instant = Instant.now(),
)
