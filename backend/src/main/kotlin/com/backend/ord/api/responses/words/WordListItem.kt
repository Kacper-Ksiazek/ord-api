package com.backend.ord.api.responses.words

import com.backend.ord.api.responses.words.embedded.BankCompact
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import java.time.Instant
import java.util.*

/**
 * Represents a single word entry in the list returned by the GET /api/vn/words endpoint.
 */
data class WordListItem(
    val id: UUID,
    var points: Int,
    var origin: String,
    var translation: String,
    var isCompleted: Boolean,
    var isBookmarked: Boolean,
    var type: WordType,
    var extraMark: WordExtraMark?,
    var translatedFrom: LanguageName,
    var translatedTo: LanguageName,
    var bankId: UUID?,
    val bank: BankCompact?, // Nested object
    val createdAt: Instant,
    var updatedAt: Instant
)
