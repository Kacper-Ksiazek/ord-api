package com.ord.testing_utils.dto.resources.db_rows

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import java.util.*

data class WordDBExportedRow(
    val type: WordType,
    val extraMark: WordExtraMark?,
    val sourceWord: String,
    val translation: String,
    val definition: String,
    val isBookmarked: Boolean = false,
    val points: Int = 0,
    val language: LanguageName,
) {
    fun convertIntoWordEntity(userId: UUID): WordEntity {
        return WordEntity(
            type = type,
            extraMark = extraMark,
            sourceWord = sourceWord,
            translation = translation,
            definition = definition,
            isBookmarked = isBookmarked,
            language = language,
            userId = userId,
        )
    }

    val progressPoints: Int get() = points
}
