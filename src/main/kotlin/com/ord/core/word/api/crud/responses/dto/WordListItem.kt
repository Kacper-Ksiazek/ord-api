package com.ord.core.word.api.crud.responses.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.bank.dto.BankCompact
import java.util.*

/**
 * Represents a single word entry in the list returned by the GET /api/vn/words endpoint.
 */
data class WordListItem(
    val id: UUID,

    var points: Int,
    var origin: String,
    var translation: String,
    var isCompleted: Boolean,
    var isBookmarked: Boolean,

    var type: WordType,
    var extraMark: WordExtraMark?,
    var translatedFrom: LanguageName,
    var translatedTo: LanguageName,

    val bank: BankCompact?,
) {
    companion object {
        val fields = setOf(
            "id",
            "points",
            "origin",
            "translation",
            "is_completed",
            "is_bookmarked",
            "type",
            "extra_mark",
            "translated_to",
            "translated_from",
            "created_at",
        )
    }
}
