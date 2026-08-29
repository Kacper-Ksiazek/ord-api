package com.ord.core.word.api.crud.responses.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_progress.WordProgressDTO
import com.ord.features.bank.dto.BankCompact
import java.util.*

data class WordListItem(
    val id: UUID,

    var status: WordStatus,
    var sourceWord: String,
    var translation: String?,
    var isBookmarked: Boolean,
    var progress: WordProgressDTO?,

    var type: WordType?,
    var extraMark: WordExtraMark?,
    var language: LanguageName,

    val bank: BankCompact?,
) {
    companion object {
        val fields = setOf(
            "id",
            "status",
            "source_word",
            "translation",
            "is_bookmarked",
            "type",
            "extra_mark",
            "language",
            "created_at",
        )
    }
}
