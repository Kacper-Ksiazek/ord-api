package com.backend.ord.utils

import com.backend.ord.shared.utils.StringUtils
import org.junit.jupiter.api.Test

class TestStringUtils {
    @Test
    fun `Add asterisks around all occurrences of a word in the entire sentences`() {
        val text = "This is a test sentence. This is another test sentence."
        val word = "test"
        val expected = "This is a *test* sentence. This is another *test* sentence."

        val actual = StringUtils.addAsteriskAroundWordInText(
            word = word,
            text = text
        )

        assert(expected == actual)
    }

    @Test
    fun `Add asterisks around word in not lower case`() {
        val text = "Test sentence. Another test sentence."
        val word = "test"
        val expected = "*Test* sentence. Another *test* sentence."

        val actual = StringUtils.addAsteriskAroundWordInText(
            word = word,
            text = text
        )

        println(expected)
        println(actual)

        assert(expected == actual)
    }

    @Test
    fun `Add asterisks around word at the end of the sentence leave dot alone`() {
        val text = "Test sentence. Another test sentence."
        val word = "sentence"
        val expected = "Test *sentence*. Another test *sentence*."

        val actual = StringUtils.addAsteriskAroundWordInText(
            word = word,
            text = text
        )

        println(expected)
        println(actual)

        assert(expected == actual)
    }

    @Test
    fun `Blocks can be added around a word`() {
        val text = "This is a test sentence. This is another test sentence."
        val word = "test"
        val prefix = "%%[{tooltip: \"Testowy blok\", id: 3123123123}]%%"
        val suffix = "%---%"

        val expected = "This is a $prefix$word$suffix sentence. This is another $prefix$word$suffix sentence."


        val actual = StringUtils.wrapWordWith(
            text = text,
            word = word,
            prefix = prefix,
            suffix = suffix
        )

        assert(expected == actual)
    }
}
