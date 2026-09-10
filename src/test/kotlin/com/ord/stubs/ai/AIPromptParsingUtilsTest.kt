package com.ord.stubs.ai

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("AIPromptParsingUtils")
class AIPromptParsingUtilsTest {
    @Test
    fun `parseQAWFillGapsItems should extract sourceWord from formatted prompt lines`() {
        val prompt = """
            ### INPUT WORDS (process in this exact order):

            1. sourceWord: "verbose" | translation: — | definition: — | type: — | extraMark: —
            2. sourceWord: "dude" | translation: "stary" | definition: — | type: "NOUN" | extraMark: "SLANG"

            ### TASK:
        """.trimIndent()

        AIPromptParsingUtils.parseQAWFillGapsItems(prompt) shouldBe listOf(
            ParsedFillGapsPromptItem(sourceWord = "verbose"),
            ParsedFillGapsPromptItem(
                sourceWord = "dude",
                translation = "stary",
                type = "NOUN",
                extraMark = "SLANG",
            ),
        )
    }

    @Test
    fun `parseQAWInputWords should return only source words`() {
        val prompt = """
            ### INPUT WORDS (process in this exact order):

            1. sourceWord: "hello" | translation: — | definition: — | type: — | extraMark: —

            ### TASK:
        """.trimIndent()

        AIPromptParsingUtils.parseQAWInputWords(prompt) shouldBe listOf("hello")
    }
}
