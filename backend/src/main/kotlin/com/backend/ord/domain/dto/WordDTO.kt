package com.backend.ord.domain.dto

import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import java.time.Instant
import java.util.*

class WordDTO(
    val id: UUID = UUID.randomUUID(),

    var origin: String,
    var translation: String,
    var definition: String,
    var useCases: Set<String> = emptySet(),
    var isBookmarked: Boolean = false,
    var type: WordType,
    var extraMark: WordExtraMark? = null,
    var translatedFrom: LanguageName,
    var translatedTo: LanguageName,

    var points: Int = 0,
    var exampleSentences: Set<ExampleSentence> = emptySet(),

    val user: UserDTO,
    var bank: BankDTO? = null,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)

