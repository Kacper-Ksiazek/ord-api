package com.ord.core.word.api.crud.responses.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_progress.WordProgressDTO
import com.ord.features.bank.dto.BankCompact
import java.time.Instant
import java.util.*

data class WordListItem(
    val id: UUID,

    var sourceWord: String,
    var translation: String,
    var definition: String?,
    var isBookmarked: Boolean,
    var progress: WordProgressDTO?,

    var type: WordType,
    var extraMark: WordExtraMark?,
    var language: LanguageName,

    val bank: BankCompact?,

    val createdAt: Instant,
) {
    companion object {
        val fields = setOf(
            "id",
            "source_word",
            "translation",
            "definition",
            "is_bookmarked",
            "type",
            "extra_mark",
            "language",
            "created_at",
        )
    }
}
