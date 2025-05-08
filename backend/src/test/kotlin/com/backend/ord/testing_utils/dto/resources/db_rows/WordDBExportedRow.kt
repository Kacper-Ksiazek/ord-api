package com.backend.ord.testing_utils.dto.resources.db_rows

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.domain.persistence.jsons.ExampleSentence
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType

data class WordDBExportedRow(
    val type: WordType,
    val extraMark: WordExtraMark?,
    val origin: String,
    val translation: String,
    val definition: String,
    val useCases: Set<String>,
    val isCompleted: Boolean,
    val isBookmarked: Boolean,
    val points: Int,
    val exampleSentences: Set<ExampleSentence>,
    val translatedFrom: LanguageName,
    val translatedTo: LanguageName
) {
    fun convertIntoWordEntity(user: User): Word {
        return Word(
            type = type,
            extraMark = extraMark,
            origin = origin,
            translation = translation,
            definition = definition,
            useCases = useCases,
            isCompleted = isCompleted,
            isBookmarked = isBookmarked,
            points = points,
            exampleSentences = exampleSentences,
            translatedFrom = translatedFrom,
            translatedTo = translatedTo,
            user = user,
            userId = user.id
        )
    }
}