package com.ord.shared.prompts.structured_outputs.base

/**
 * JSON Schema definition for OpenAI structured outputs.
 * All objects in the schema must have additionalProperties set to false.
 * All fields must be required (OpenAI constraint).
 */
data class StructuredOutputTemplate(
    val name: String,
    val schema: Map<String, Any>,
) {
    val type: String = "json_schema"
    val strict: Boolean = true
}