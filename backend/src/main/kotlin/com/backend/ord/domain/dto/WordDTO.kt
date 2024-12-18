package com.backend.ord.domain.dto

import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.word.WordExtraMark
import com.backend.ord.enums.word.WordType
import java.time.Instant
import java.util.*

class WordDTO(
    val id: UUID = UUID.randomUUID(),

    var origin: String,
    var translation: String,
    var definition: String,
    var useCases: Set<String> = emptySet(),
    var isBookmarked: Boolean = false,
    var completed: Boolean = false,
    var type: WordType,
    var extraMark: WordExtraMark? = null,
    var translatedFrom: LanguageName,
    var translatedTo: LanguageName,

    var points: Int = 0,
    var exampleSentences: Set<ExampleSentence> = emptySet(),

    val user: UserDTO,
    val userId: UUID = user.id,

    var bank: BankDTO? = null,
    var bankId: UUID? = bank?.id,

    var bankGroupId: UUID? = bank?.bankGroupId,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)