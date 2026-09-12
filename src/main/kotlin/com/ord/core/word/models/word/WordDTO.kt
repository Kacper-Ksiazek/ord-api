package com.ord.core.word.models.word

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_progress.WordProgressDTO
import com.ord.features.bank.model.BankDTO
import java.time.Instant
import java.util.*

class WordDTO(
    val id: UUID = UUID.randomUUID(),

    var type: WordType,
    var sourceWord: String,
    var translation: String,
    var definition: String? = null,
    var extraMark: WordExtraMark? = null,

    var language: LanguageName,

    var isBookmarked: Boolean = false,

    val userId: UUID,

    var bank: BankDTO? = null,
    var bankId: UUID? = bank?.id,
    var bankGroupId: UUID? = bank?.groupId,

    var progress: WordProgressDTO? = null,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
