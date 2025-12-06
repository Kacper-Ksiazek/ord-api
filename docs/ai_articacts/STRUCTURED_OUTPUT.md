# Structured Output Implementation Guide

This document provides a comprehensive guide for implementing OpenAI structured outputs in the application.

## Overview

OpenAI structured outputs require special handling because they don't support nullable fields. This necessitates an intermediate DTO pattern to convert between OpenAI's format and our domain models.

## Architecture Pattern

```
OpenAI API Response → Intermediate DTO → Domain Model
```

**Key Principle:** OpenAI DTOs use empty strings/objects/arrays to represent null values, which are then converted to proper nulls in domain models.

## Implementation Steps

### 1. Create JSON Schema Definition

Location: `src/main/kotlin/com/ord/shared/prompts/structured_outputs/features/{feature}/{Feature}Schema.kt`

**Important Rules:**
- All top-level and nested fields must be in the `required` array
- Use `enum` with `.entries` property for enum fields (never hardcode enum values)
- Objects with `additionalProperties` must include empty `properties` map
- Include descriptions for all fields

**Example:**
```kotlin
package com.ord.shared.prompts.structured_outputs.features.words

import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val generatedWordManualSchema = StructuredOutputTemplate(
    name = "word_manual",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "word" to mapOf(
                "type" to "string",
                "description" to "The word being defined"
            ),
            "type" to mapOf(
                "type" to "string",
                "enum" to WordType.entries,  // ✅ Use .entries
                "description" to "Type of word"
            ),
            "metadata" to mapOf(
                "type" to "object",
                "description" to "Dynamic metadata map",
                "additionalProperties" to mapOf("type" to "string"),
                "properties" to mapOf<String, Any>()  // ✅ Required for additionalProperties
            )
        ),
        "required" to listOf("word", "type", "metadata"),  // ✅ All fields required
        "additionalProperties" to false
    )
)
```

### 2. Create Intermediate DTO

Location: `src/main/kotlin/com/ord/core/{domain}/api/ai/responses/openai/OpenAI{Feature}.kt`

**Pattern:**
- All fields non-nullable
- Empty strings for nullable string fields
- Empty objects/arrays for nullable complex types
- Include `toDomain()` conversion method
- Add comprehensive documentation

**Example:**
```kotlin
package com.ord.core.word.api.ai.responses.openai

/**
 * Intermediate DTO for OpenAI structured outputs response.
 *
 * This DTO matches OpenAI's structured output schema where all fields are required.
 * Nullable fields use empty string ("") or empty objects to represent null values,
 * which are then mapped to proper nulls in the domain model via `toDomain()`.
 */
data class OpenAIGeneratedWordManual(
    val word: String,
    val suggestedCorrection: String,  // Empty string = null
    val type: WordType,
    val extraMark: String,  // Empty string = null, otherwise enum value
    val metadata: Map<String, String>  // Empty map = null
) {
    fun toDomain(): AIGeneratedWordManual {
        return AIGeneratedWordManual(
            word = word,
            suggestedCorrection = suggestedCorrection.ifEmpty { null },
            type = type,
            extraMark = if (extraMark.isEmpty()) null else WordExtraMark.valueOf(extraMark),
            metadata = metadata.ifEmpty { null }
        )
    }
}
```

### 3. Connect Schema to Prompt

Location: `src/main/kotlin/com/ord/shared/prompts/AvailablePrompts.kt`

**Steps:**
1. Import the schema
2. Add `structuredOutput` parameter to enum entry

```kotlin
import com.ord.shared.prompts.structured_outputs.features.words.generatedWordManualSchema

enum class AvailablePrompts(
    val resourcePath: String,
    val structuredOutput: StructuredOutputTemplate? = null,
) {
    WORDS_GENERATE_MANUAL(
        resourcePath = "words/generate_word_manual.md",
        structuredOutput = generatedWordManualSchema  // ✅ Connect schema
    ),
}
```

### 4. Update Facade Implementation

Location: `src/main/kotlin/com/ord/core/{domain}/api/ai/facades/impl/{Domain}AIFacadeImpl.kt`

**Changes:**
1. Import intermediate DTO
2. Change `TypeReference` to use intermediate DTO
3. Call `.toDomain()` to convert response

```kotlin
import com.ord.core.word.api.ai.responses.openai.OpenAIGeneratedWordManual

openAIAPIClientService
    .makeRequest(
        aiResponseType = object : TypeReference<OpenAIGeneratedWordManual>() {},  // ✅ Intermediate DTO
        prompt = prompt,
        userId = user.id,
        gptTokensUsageLogKey = GptTokensUsageOperationType.Words.GENERATE_MANUAL,
    )
    .map { it.toDomain() }  // ✅ Convert to domain model
```

## Common Patterns

### Handling Nested Objects

When a nested object can be null, use an indicator field (e.g., empty string for required field):

```kotlin
data class OpenAIPronunciation(
    val ipa: String,  // Empty = entire object is null
    val syllables: String,
    val stress: Int
) {
    fun toDomain(): WordPronunciation? {
        if (ipa.isEmpty()) return null  // ✅ Check indicator field

        return WordPronunciation(
            ipa = ipa,
            syllables = syllables.ifEmpty { null },
            stress = if (stress == 0) null else stress
        )
    }
}
```

### Handling Complex Objects with Multiple Nullable Fields

Check if any field has content to determine if object should be null:

```kotlin
fun toDomain(): WordGrammar? {
    val hasContent = gender.isNotEmpty() ||
                    definiteArticle.isNotEmpty() ||
                    pluralForm.isNotEmpty()

    if (!hasContent) return null

    return WordGrammar(...)
}
```

## Checklist for New Structured Output

- [ ] Create schema file with `.entries` for all enums
- [ ] Add empty `properties` map to objects with `additionalProperties`
- [ ] Mark ALL fields as required in schema
- [ ] Create intermediate DTO with all non-nullable fields
- [ ] Implement `toDomain()` conversion method
- [ ] Add schema to `AvailablePrompts` enum
- [ ] Update facade to use intermediate DTO
- [ ] Test with actual API calls

## Reference Examples

- **Conversation Review:** `OpenAIReviewedMessage.kt` and `ReviewedUserConversationMessageSchema.kt`
- **Word Manual:** `OpenAIGeneratedWordManual.kt` and `GeneratedWordManualSchema.kt`