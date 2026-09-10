package com.ord.stubs.ai

import java.util.UUID

data class ParsedFillGapsPromptItem(
    val sourceWord: String,
    val translation: String? = null,
    val definition: String? = null,
    val type: String? = null,
    val extraMark: String? = null,
)

object AIPromptParsingUtils {
    private val NUMBERED_WORD_LINE = Regex("""^\d+\.\s+(.+)$""", RegexOption.MULTILINE)
    private val BULLET_WORD_LINE = Regex("""^-\s+(.+)$""", RegexOption.MULTILINE)
    private val FILL_GAPS_LINE_FIELD = Regex("""(\w+):\s*(?:—|"([^"]*)")""")
    private val TARGET_WORD = Regex("""Target Word:\s*(.+)""", RegexOption.MULTILINE)
    private val AMOUNT_OF_QUESTIONS = Regex("""Generate\s+(\d+)\s+""", RegexOption.IGNORE_CASE)
    private val TOPIC_ID = Regex("""Topic ID:\s*([0-9a-fA-F-]{36})""")

    fun parseNumberedWords(prompt: String): List<String> =
        NUMBERED_WORD_LINE.findAll(prompt).map { it.groupValues[1].trim() }.toList()

    fun parseQAWInputWords(prompt: String): List<String> =
        parseQAWFillGapsItems(prompt).map { it.sourceWord }

    fun parseQAWFillGapsItems(prompt: String): List<ParsedFillGapsPromptItem> {
        val wordsSection = prompt
            .substringAfter("INPUT WORDS (process in this exact order):", "")
            .substringBefore("### TASK:")

        return NUMBERED_WORD_LINE.findAll(wordsSection).map { match ->
            parseFillGapsPromptLine(match.groupValues[1].trim())
        }.toList()
    }

    private fun parseFillGapsPromptLine(line: String): ParsedFillGapsPromptItem {
        if (!line.contains("sourceWord:")) {
            return ParsedFillGapsPromptItem(sourceWord = line)
        }

        val fields = mutableMapOf<String, String?>()
        FILL_GAPS_LINE_FIELD.findAll(line).forEach { match ->
            fields[match.groupValues[1]] = match.groupValues.getOrNull(2)?.trim()?.takeIf { it.isNotEmpty() }
        }

        return ParsedFillGapsPromptItem(
            sourceWord = fields["sourceWord"] ?: line,
            translation = fields["translation"],
            definition = fields["definition"],
            type = fields["type"],
            extraMark = fields["extraMark"],
        )
    }

    fun parseBulletWords(prompt: String): List<String> {
        val wordsSection = prompt.substringAfter("Words for you to use:", "")
        return BULLET_WORD_LINE.findAll(wordsSection).map { it.groupValues[1].trim() }.toList()
    }

    fun parseTargetWord(prompt: String): String? =
        TARGET_WORD.find(prompt)?.groupValues?.get(1)?.trim()

    fun parseAmountOfQuestions(prompt: String): Int? =
        AMOUNT_OF_QUESTIONS.find(prompt)?.groupValues?.get(1)?.toIntOrNull()

    private val WORD_COUNT = Regex("""Generate\s+(\d+)\s+new vocabulary""", RegexOption.IGNORE_CASE)

    fun parseWordCount(prompt: String): Int =
        WORD_COUNT.find(prompt)?.groupValues?.get(1)?.toIntOrNull() ?: 10

    fun parseExistingWords(prompt: String): List<String> =
        parseCommaSeparatedWordList(
            prompt = prompt,
            sectionStart = "USER'S EXISTING VOCABULARY:",
            sectionEnd = "### PREVIOUSLY SUGGESTED WORDS:",
        )

    fun parseExcludedWords(prompt: String): List<String> =
        parseCommaSeparatedWordList(
            prompt = prompt,
            sectionStart = "PREVIOUSLY SUGGESTED WORDS:",
            sectionEnd = "### GUIDELINES:",
        )

    private fun parseCommaSeparatedWordList(
        prompt: String,
        sectionStart: String,
        sectionEnd: String,
    ): List<String> {
        val section = prompt
            .substringAfter(sectionStart, "")
            .substringBefore(sectionEnd)
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filterNot { it.startsWith("The following") || it.startsWith("**CRITICAL") }
            .joinToString("\n")

        if (section.isBlank() || section.startsWith("No ")) {
            return emptyList()
        }

        return section.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun parseTopicIds(prompt: String): List<UUID> =
        TOPIC_ID.findAll(prompt).map { UUID.fromString(it.groupValues[1]) }.toList()
}
