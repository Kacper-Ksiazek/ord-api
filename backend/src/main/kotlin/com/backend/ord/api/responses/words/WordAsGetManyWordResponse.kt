package com.backend.ord.api.responses.words

import com.backend.ord.api.responses.words.embedded.BankCompact
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import java.time.Instant
import java.util.UUID

data class WordAsGetManyWordResponse(
    val id: UUID,
    var points: Int,
    var origin: String,
    var translation: String,
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

