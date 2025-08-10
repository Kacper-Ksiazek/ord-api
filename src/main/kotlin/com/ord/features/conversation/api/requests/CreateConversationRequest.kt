package com.ord.features.conversation.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.models.enums.ConversationAIResponseLength
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.features.conversation.models.enums.ConversationTone

data class CreateConversationRequest(
    val topic: String,
    val additionalContext: String? = null,

    val language: LanguageName,
    val proficiencyLevel: LanguageProficiencyLevel,

    val tone: ConversationTone,
    val goal: ConversationGoal,
    val aiResponseLength: ConversationAIResponseLength,
)