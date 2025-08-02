package com.ord.features.conversation.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.models.enums.ConversationGoal

data class SuggestConversationTopicRequest(
    val clueFromUser: String? = null,
    val conversationGoal: ConversationGoal,
    val languageProficiencyLevel: LanguageProficiencyLevel
)