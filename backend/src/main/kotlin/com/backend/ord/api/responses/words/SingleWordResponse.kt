package com.backend.ord.api.responses.words

import com.backend.ord.api.responses.words.embedded.BankCompact
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import java.time.Instant
import java.util.UUID

data class SingleWordResponse(
    val id: UUID,

    var points: Int,
    var origin: String,
    var translation: String,
    var definition: String,
    var isBookmarked: Boolean,

    var type: WordType,
    var extraMark: WordExtraMark?,
    var translatedTo: LanguageName,
    var translatedFrom: LanguageName,

    var useCases: Set<String>,
    var exampleSentences: Set<ExampleSentence>,

    var bank: BankCompact?,

    val createdAt: Instant,
    var updatedAt: Instant,
)