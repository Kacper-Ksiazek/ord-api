package com.ord.core.word.api.ai

import com.ord.core.word.api.ai.requests.dto.WordFillGapsItem
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("WordFillGapsPromptFormatter")
class WordFillGapsPromptFormatterTest {
    @Test
    fun `should mark missing optional fields with em dash`() {
        val formatted = WordFillGapsPromptFormatter.formatItemsForPrompt(
            listOf(WordFillGapsItem(sourceWord = "verbose")),
        )

        formatted shouldBe
            "1. sourceWord: \"verbose\" | translation: — | definition: — | type: — | extraMark: —"
    }

    @Test
    fun `should include provided optional fields in prompt line`() {
        val formatted = WordFillGapsPromptFormatter.formatItemsForPrompt(
            listOf(
                WordFillGapsItem(
                    sourceWord = "dude",
                    translation = "stary",
                    type = WordType.NOUN,
                    extraMark = WordExtraMark.SLANG,
                ),
            ),
        )

        formatted shouldBe
            "1. sourceWord: \"dude\" | translation: \"stary\" | definition: — | type: \"NOUN\" | extraMark: \"SLANG\""
    }
}
