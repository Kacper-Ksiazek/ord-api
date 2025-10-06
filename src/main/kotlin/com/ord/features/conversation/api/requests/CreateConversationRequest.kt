package com.ord.features.conversation.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.enums.ConversationType
import com.ord.features.conversation.models.enums.ConversationTone
import com.ord.features.conversation.validators.annotations.ValidConversationAIBotAvatarId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateConversationRequest(
    @field:NotBlank(message = "Topic cannot be blank")
    @field:Size(min = 1, max = 500, message = "Topic must be between 1 and 500 characters")
    val topic: String,

    @field:Size(max = 5000, message = "Additional context must be less than 5000 characters")
    val additionalContext: String? = null,

    val language: LanguageName,

    val tone: ConversationTone,
    val type: ConversationType,

    @field:Size(max = 200, message = "AI interlocutor name must be less than 200 characters")
    val aiInterlocutorName: String? = null,

    @field:ValidConversationAIBotAvatarId
    val aiInterlocutorAvatarId: String? = null,
)