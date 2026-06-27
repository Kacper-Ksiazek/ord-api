# Follow the structured-output schema conventions

Schemas are `StructuredOutputTemplate(name, schema)` (which always sets `type = "json_schema"` and `strict = true`). The root must be an object, every property must be listed in `required`, and every object must set `additionalProperties` to `false`. Provide a `description` for each field.

## Good

```kotlin
val generatedAIInterlocutorSchema = StructuredOutputTemplate(
    name = "ai_interlocutor_generate",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "name" to mapOf(
                "type" to "string",
                "description" to "A culturally appropriate full name for the AI interlocutor"
            ),
            "avatarId" to mapOf(
                "type" to "string",
                "description" to "Avatar ID selected from the provided list",
                "enum" to ConversationAIBotAvatar.toSchemaEnumList()
            )
        ),
        "required" to listOf("name", "avatarId"),
        "additionalProperties" to false
    )
)
```

## Bad

```kotlin
val generatedAIInterlocutorSchema = StructuredOutputTemplate(
    name = "ai_interlocutor_generate",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "name" to mapOf("type" to "string"),       // no description
            "avatarId" to mapOf("type" to "string")
        ),
        "required" to listOf("name"),                  // avatarId missing from required
        // additionalProperties omitted -> not allowed for strict schemas
    )
)
```
