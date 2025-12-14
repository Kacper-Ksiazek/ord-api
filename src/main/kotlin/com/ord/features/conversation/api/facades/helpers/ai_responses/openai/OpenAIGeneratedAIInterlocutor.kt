package com.ord.features.conversation.api.facades.helpers.ai_responses.openai

import com.ord.features.conversation.api.facades.helpers.ai_responses.GeneratedAIInterlocutorData
import com.ord.features.conversation.models.conversation.enums.ConversationAIBotAvatar
import org.slf4j.LoggerFactory

/**
 * Intermediate DTO for OpenAI structured outputs response for AI interlocutor generation.
 *
 * This DTO matches OpenAI's structured output schema where all fields are required.
 * The avatarId is returned as a string and validated/converted to the ConversationAIBotAvatar
 * enum in the toDomain() method.
 *
 * Use `toDomain()` to convert this to `GeneratedAIInterlocutorData` for application use.
 */
data class OpenAIGeneratedAIInterlocutor(
    val name: String,
    val avatarId: String
) {
    /**
     * Converts OpenAI response to domain model.
     *
     * Sanitizes avatarId to handle minor formatting issues (spaces, case).
     * Throws IllegalArgumentException if avatarId is invalid after sanitization,
     * which triggers retry logic in the OpenAI service.
     */
    fun toDomain(): GeneratedAIInterlocutorData {
        // Sanitize avatarId to handle potential AI formatting issues (e.g., extra spaces)
        val sanitizedAvatarId = avatarId
            .trim()
            .replace("\\s+".toRegex(), "_")
            .uppercase()

        return GeneratedAIInterlocutorData(
            name = name.trim(),
            avatarId = ConversationAIBotAvatar.valueOf(sanitizedAvatarId) // Throws if invalid
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(OpenAIGeneratedAIInterlocutor::class.java)
    }
}
