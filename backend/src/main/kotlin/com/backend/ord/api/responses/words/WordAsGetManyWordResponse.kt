package com.backend.ord.api.responses.words

import com.backend.ord.domain.dto.BankDTO
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.domain.entities.Bank
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

//    var bank: Bank?,
    var bankId: UUID?,

    val createdAt: Instant,
    var updatedAt: Instant
)