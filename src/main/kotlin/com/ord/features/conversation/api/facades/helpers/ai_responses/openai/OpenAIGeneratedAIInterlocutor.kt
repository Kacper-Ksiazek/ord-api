package com.ord.features.conversation.api.facades.helpers.ai_responses.openai

import com.ord.features.conversation.api.facades.helpers.ai_responses.GeneratedAIInterlocutorData
import com.ord.features.conversation.models.conversation.enums.ConversationAIBotAvatar

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
    fun toDomain(): GeneratedAIInterlocutorData {
        return GeneratedAIInterlocutorData(
            name = name,
            avatarId = ConversationAIBotAvatar.valueOf(avatarId)
        )
    }
}
