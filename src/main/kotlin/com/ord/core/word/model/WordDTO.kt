package com.ord.core.word.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.features.bank.model.BankDTO
import java.time.Instant
import java.util.*

class WordDTO(
    val id: UUID = UUID.randomUUID(),

    var origin: String,
    var points: Int = 0,
    var definition: String,
    var translation: String,
    var isCompleted: Boolean = false,
    var isBookmarked: Boolean = false,
    var type: WordType,
    var translatedTo: LanguageName,
    var translatedFrom: LanguageName,
    var extraMark: WordExtraMark? = null,

    var useCases: Set<String> = emptySet(),
    var exampleSentences: Set<ExampleSentence> = emptySet(),

    val user: UserDTO,
    val userId: UUID = user.id,

    var bank: BankDTO? = null,
    var bankId: UUID? = bank?.id,

    var bankGroupId: UUID? = bank?.bankGroupId,

    var completedAt: Instant? = null,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)