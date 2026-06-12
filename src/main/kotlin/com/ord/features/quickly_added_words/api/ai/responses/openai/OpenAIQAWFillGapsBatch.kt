package com.ord.features.quickly_added_words.api.ai.responses.openai

import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.quickly_added_words.api.responses.QAWFillGapsResponse
import com.ord.features.quickly_added_words.api.responses.QAWFillGapsResultItem
import com.ord.shared.prompts.structured_outputs.StructuredOutputUtils

/**
 * Intermediate DTO for OpenAI structured outputs response for QAW fill-gaps batch.
 *
 * All fields are required in the schema; empty strings represent absent optional values.
 * Use [toDomain] to convert to [QAWFillGapsResponse] for application use.
 */
data class OpenAIQAWFillGapsBatch(
    val items: List<OpenAIQAWFillGapsItem>,
) {
    fun toDomain(): QAWFillGapsResponse {
        return QAWFillGapsResponse(items = items.map { it.toDomain() })
    }
}

data class OpenAIQAWFillGapsItem(
    val inputWord: String,
    val word: String,
    val translation: String,
    val definition: String,
    val type: String,
    val extraMark: String,
    val error: String,
) {
    fun toDomain(): QAWFillGapsResultItem {
        val errorCode = StructuredOutputUtils.sanitizeNullableStringValue(error)
        if (errorCode != null) {
            return QAWFillGapsResultItem(
                inputWord = inputWord,
                word = null,
                translation = null,
                definition = null,
                type = null,
                extraMark = null,
                error = errorCode,
            )
        }

        return QAWFillGapsResultItem(
            inputWord = inputWord,
            word = StructuredOutputUtils.sanitizeNullableStringValue(word),
            translation = StructuredOutputUtils.sanitizeNullableStringValue(translation),
            definition = StructuredOutputUtils.sanitizeNullableStringValue(definition)?.take(2000),
            type = StructuredOutputUtils.sanitizeNullableStringValue(type)?.let { WordType.valueOf(it) },
            extraMark = StructuredOutputUtils.sanitizeNullableStringValue(extraMark)?.let { WordExtraMark.valueOf(it) },
            error = null,
        )
    }
}
