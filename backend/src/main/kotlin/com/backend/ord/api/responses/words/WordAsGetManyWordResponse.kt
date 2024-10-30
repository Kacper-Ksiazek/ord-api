package com.backend.ord.api.responses.words

import com.backend.ord.domain.dto.BankDTO
import com.backend.ord.domain.embedded.ExampleSentence
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
    var definition: String,
    var useCases: Set<String>,
    var isBookmarked: Boolean,

    var type: WordType,
    var extraMark: WordExtraMark?,
    var translatedFrom: LanguageName,
    var translatedTo: LanguageName,

    var exampleSentences: Set<ExampleSentence>,

    var bank: BankDTO?,

    val createdAt: Instant,
    var updatedAt: Instant
)