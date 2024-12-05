package com.backend.ord.utils

object StringUtils {
    fun addAsteriskAroundWordInText(text: String, word: String): String = wrapWordWith(
        text = text,
        word = word,
        prefix = "*",
        suffix = "*"
    )

    fun wrapWordWith(
        text: String,
        word: String,
        prefix: String,
        suffix: String
    ): String {
        val wrappedWord = "$prefix$word$suffix"

        val capitalizedWord = word.replaceFirstChar { it.uppercase() }
        val wrappedCapitalizedWord = "$prefix$capitalizedWord$suffix"

        return text.split(' ').joinToString(
            separator = " ",
            transform = {
                if (it.contains(word)) it.replace(word, wrappedWord)
                else if (it.contains(capitalizedWord)) it.replace(capitalizedWord, wrappedCapitalizedWord)
                else it
            }
        )
    }
}