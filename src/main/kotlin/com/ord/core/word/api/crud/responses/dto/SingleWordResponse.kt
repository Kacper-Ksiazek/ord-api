package com.ord.core.word.api.crud.responses.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_details.WordDetailsCompactDTO
import com.ord.features.bank.dto.BankCompact
import java.time.Instant
import java.util.*

data class SingleWordResponse(
    val id: UUID,

    var type: WordType,
    var sourceWord: String,
    var translation: String,
    var definition: String,
    var extraMark: WordExtraMark?,

    var language: LanguageName,

    var isCompleted: Boolean,
    var isBookmarked: Boolean,

    var points: Int,

    var bank: BankCompact?,

    val createdAt: Instant,
    var updatedAt: Instant,

    var details: WordDetailsCompactDTO? = null,
) {
    companion object {
        val fields = setOf(
            "id",
            "type",
            "source_word",
            "translation",
            "definition",
            "extra_mark",
            "language",
            "is_completed",
            "is_bookmarked",
            "points",
            "created_at",
            "updated_at"
        )
    }
}