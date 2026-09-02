package com.ord.core.word.api.ai.responses.openai

import com.ord.core.word.api.ai.responses.dto.WordFillGapsResponse
import com.ord.core.word.api.ai.responses.dto.WordFillGapsResultItem
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.prompts.structured_outputs.StructuredOutputUtils

data class OpenAIWordFillGapsBatch(
    val items: List<OpenAIWordFillGapsItem>,
) {
    fun toDomain(): WordFillGapsResponse = WordFillGapsResponse(items = items.map { it.toDomain() })
}

data class OpenAIWordFillGapsItem(
    val inputWord: String,
    val word: String,
    val translation: String,
    val definition: String,
    val type: String,
    val extraMark: String,
    val error: String,
) {
    fun toDomain(): WordFillGapsResultItem {
        val errorCode = StructuredOutputUtils.sanitizeNullableStringValue(error)
        if (errorCode != null) {
            return WordFillGapsResultItem(
                inputSourceWord = inputWord,
                sourceWord = null,
                translation = null,
                definition = null,
                type = null,
                extraMark = null,
                error = errorCode,
            )
        }

        return WordFillGapsResultItem(
            inputSourceWord = inputWord,
            sourceWord = StructuredOutputUtils.sanitizeNullableStringValue(word),
            translation = StructuredOutputUtils.sanitizeNullableStringValue(translation),
            definition = StructuredOutputUtils.sanitizeNullableStringValue(definition)?.take(2000),
            type = StructuredOutputUtils.sanitizeNullableStringValue(type)?.let { WordType.valueOf(it) },
            extraMark = StructuredOutputUtils.sanitizeNullableStringValue(extraMark)?.let { WordExtraMark.valueOf(it) },
            error = null,
        )
    }
}
