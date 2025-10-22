package com.ord.testing_utils.dto.resources.db_rows

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import java.util.*

data class WordDBExportedRow(
    val type: WordType,
    val extraMark: WordExtraMark?,
    val origin: String,
    val translation: String,
    val definition: String,
    val isCompleted: Boolean,
    val isBookmarked: Boolean,
    val points: Int,
    val translatedFrom: LanguageName,
    val translatedTo: LanguageName
) {
    private val objectMapper = jacksonObjectMapper()

    fun convertIntoWordEntity(userId: UUID): WordEntity {
        return WordEntity(
            type = type,
            extraMark = extraMark,
            origin = origin,
            translation = translation,
            definition = definition,
            isCompleted = isCompleted,
            isBookmarked = isBookmarked,
            points = points,
            translatedFrom = translatedFrom,
            translatedTo = translatedTo,
            userId = userId
        )
    }
}