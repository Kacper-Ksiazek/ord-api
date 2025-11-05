package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.conversation.enums.ConversationAIBotAvatar

data class GeneratedAIInterlocutorData(
    val name: String,
    val avatarId: ConversationAIBotAvatar
)