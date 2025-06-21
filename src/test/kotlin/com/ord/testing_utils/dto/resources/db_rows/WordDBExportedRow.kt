package com.ord.testing_utils.dto.resources.db_rows

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence

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
    fun convertIntoWordEntity(user: UserEntity): WordEntity {
        return WordEntity(
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