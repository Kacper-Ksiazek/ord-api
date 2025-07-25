package com.ord.utils

import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import io.kotest.matchers.shouldBe
import org.junit.Test

class TestPrompts {
    @Test
    fun `Test`() {
        val prompt = Prompt(
            variant = AvailablePrompts.GAMES_GENERATE_CROSSWORD,
            mapOf(
                "language" to "ENGLISH",
                "difficulty" to "HARD",
                "proficiency" to  "C1",
                "words" to listOf("foo", "bar", "lorem", "ipsum").toParamString(true)
            )
        )

        println(prompt);

        1 shouldBe 1;
    }
}