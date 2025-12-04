package com.ord.core.ai_provider.dto

/**
 * JSON Schema definition for OpenAI structured outputs.
 * All objects in the schema must have additionalProperties set to false.
 * All fields must be required (OpenAI constraint).
 */
data class JsonSchemaDefinition(
    val type: String = "json_schema",
    val name: String,
    val schema: Map<String, Any>,
    val strict: Boolean = true
)
