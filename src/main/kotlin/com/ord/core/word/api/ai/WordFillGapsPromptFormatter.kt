package com.ord.core.word.api.ai

import com.ord.core.word.api.ai.requests.dto.WordFillGapsItem

object WordFillGapsPromptFormatter {
    fun formatItemsForPrompt(items: List<WordFillGapsItem>): String =
        items.mapIndexed { index, item -> formatItem(index, item) }.joinToString(separator = "\n")

    private fun formatItem(index: Int, item: WordFillGapsItem): String {
        val fields = listOf(
            "sourceWord: \"${item.sourceWord}\"",
            formatOptionalField("translation", item.translation),
            formatOptionalField("definition", item.definition),
            formatOptionalField("type", item.type?.name),
            formatOptionalField("extraMark", item.extraMark?.name),
        )

        return "${index + 1}. ${fields.joinToString(" | ")}"
    }

    private fun formatOptionalField(label: String, value: String?): String {
        val normalized = value?.trim()?.takeIf { it.isNotEmpty() }

        return if (normalized == null) {
            "$label: —"
        } else {
            "$label: \"$normalized\""
        }
    }
}
