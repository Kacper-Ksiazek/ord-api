package com.ord.shared.prompts.structured_outputs

/**
 * Utilities for handling OpenAI structured outputs.
 *
 * These utilities help convert OpenAI's required fields (which use empty strings or special values
 * to represent null) into proper nullable types for the domain model.
 */
object StructuredOutputUtils {
    /**
     * Punctuation and whitespace characters used to detect empty-like string values.
     * Stored as a Set for O(1) lookup performance.
     */
    private val PUNCTUATION_CHARS: Set<Char> = setOf(
        ',', '.', ';', ':', '!', '?', '-', '_', '/', '\\', '|', '*', '#', '@',
        '$', '%', '^', '&', '(', ')', '[', ']', '{', '}', '"', '\'', '`', '~',
        '+', '=', '<', '>', ' ', '\t', '\n', '\r'
    )

    /**
     * Converts various empty-like values to null.
     *
     * OpenAI sometimes returns empty strings, "null" variations, "n/a", "none",
     * or punctuation marks to represent null values in structured outputs.
     *
     * @param value The string value to sanitize
     * @return null if the value is empty-like, otherwise the original string
     */
    fun sanitizeNullableStringValue(value: String?): String? {
        if (value == null) return null

        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> null
            // Check if the string contains "null" (case insensitive)
            trimmed.contains("null", ignoreCase = true) -> null
            trimmed.equals("n/a", ignoreCase = true) -> null
            trimmed.equals("none", ignoreCase = true) -> null
            // Check if string is only punctuation and/or whitespace
            trimmed.all { it in PUNCTUATION_CHARS } -> null
            else -> value
        }
    }
}