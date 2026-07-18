package com.ord.testing_utils.mocks.ai

import java.util.UUID

object AIPromptParsingUtils {
    private val NUMBERED_WORD_LINE = Regex("""^\d+\.\s+(.+)$""", RegexOption.MULTILINE)
    private val BULLET_WORD_LINE = Regex("""^-\s+(.+)$""", RegexOption.MULTILINE)
    private val TARGET_WORD = Regex("""Target Word:\s*(.+)""", RegexOption.MULTILINE)
    private val AMOUNT_OF_QUESTIONS = Regex("""Generate\s+(\d+)\s+""", RegexOption.IGNORE_CASE)
    private val TOPIC_ID = Regex("""Topic ID:\s*([0-9a-fA-F-]{36})""")

    fun parseNumberedWords(prompt: String): List<String> =
        NUMBERED_WORD_LINE.findAll(prompt).map { it.groupValues[1].trim() }.toList()

    fun parseBulletWords(prompt: String): List<String> {
        val wordsSection = prompt.substringAfter("Words for you to use:", "")
        return BULLET_WORD_LINE.findAll(wordsSection).map { it.groupValues[1].trim() }.toList()
    }

    fun parseTargetWord(prompt: String): String? =
        TARGET_WORD.find(prompt)?.groupValues?.get(1)?.trim()

    fun parseAmountOfQuestions(prompt: String): Int? =
        AMOUNT_OF_QUESTIONS.find(prompt)?.groupValues?.get(1)?.toIntOrNull()

    fun parseTopicIds(prompt: String): List<UUID> =
        TOPIC_ID.findAll(prompt).map { UUID.fromString(it.groupValues[1]) }.toList()
}
