package com.ord.shared.prompts.structured_outputs.features.conversation

import com.ord.features.conversation.models.conversation.enums.ConversationAIBotAvatar
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val generatedAIInterlocutorSchema = StructuredOutputTemplate(
    name = "ai_interlocutor_generate",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "name" to mapOf(
                "type" to "string",
                "description" to "A culturally appropriate full name for the AI interlocutor (with optional titles like Dr. or Prof. when appropriate)"
            ),
            "avatarId" to mapOf(
                "type" to "string",
                "description" to "Avatar ID selected from the provided list that best matches the conversation context. Must be one of the exact enum values from the available avatars list.",
                "enum" to ConversationAIBotAvatar.toSchemaEnumList()
            )
        ),
        "required" to listOf("name", "avatarId"),
        "additionalProperties" to false
    )
)
