package com.ord.features.conversation.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.conversation.models.enums.ConversationType
import com.ord.features.conversation.models.enums.ConversationTone

data class CreateConversationRequest(
    val topic: String,
    val additionalContext: String? = null,

    val language: LanguageName,

    val tone: ConversationTone,
    val type: ConversationType,
    val aiInterlocutorName: String? = null,
    val aiInterlocutorAvatarId: String? = null,
)